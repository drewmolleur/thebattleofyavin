package View;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A streaming decoder for animated GIF files.
 *
 * It reads one frame at a time from the stream and composites it onto an
 * ARGB canvas, honouring frame offsets, local colour tables, interlacing,
 * transparency and the disposal methods. Memory use is bounded by the
 * logical screen size, not the file size, and the whole file is never held
 * in memory. Looping is done by reopening the stream.
 *
 * Not thread-safe: one thread at a time may call {@link #nextFrame}.
 */
final class GifDecoder implements Closeable {

    private static final int MAX_CODES = 4096;

    private final URL location;
    private InputStream in;

    /** Logical screen size. */
    final int width, height;
    /** Loop count from the NETSCAPE extension: 0 = forever, -1 = none present (play once). */
    int loopCount = -1;

    /** Composited image, one ARGB int per pixel; 0 means "nothing drawn here". */
    final int[] canvas;

    private final int[] globalPalette;
    private final int backgroundIndex;

    // Graphic Control Extension state for the frame about to be read.
    private int gceDelayMs = 0;
    private int gceDisposal = 0;
    private int gceTransparentIndex = -1;

    // Disposal to apply before drawing the next frame, and the area it covers.
    private int pendingDisposal = 0;
    private int prevLeft, prevTop, prevWidth, prevHeight;
    private int[] savedArea;   // for disposal 3 (restore to previous)

    // Reusable working buffers.
    private final int[] localPalette = new int[256];
    private byte[] frameIndices = new byte[0];
    private byte[] lzwData = new byte[0];
    private final short[] prefix = new short[MAX_CODES];
    private final byte[] suffix = new byte[MAX_CODES];
    private final byte[] pixelStack = new byte[MAX_CODES + 1];

    GifDecoder(URL location) throws IOException {
        this.location = location;
        this.in = open();
        byte[] header = readFully(new byte[6]);
        String sig = new String(header, 0, 6, "ISO-8859-1");
        if (!sig.equals("GIF87a") && !sig.equals("GIF89a")) {
            throw new IOException("Not a GIF file: " + location);
        }
        width = readShort();
        height = readShort();
        int packed = readByte();
        backgroundIndex = readByte();
        readByte(); // pixel aspect ratio
        if ((packed & 0x80) != 0) {
            globalPalette = readPalette(2 << (packed & 7));
        } else {
            globalPalette = null;
        }
        canvas = new int[width * height];
    }

    private InputStream open() throws IOException {
        return new BufferedInputStream(location.openStream(), 1 << 16);
    }

    /**
     * Decodes the next frame onto {@link #canvas}.
     *
     * @return the frame's delay in milliseconds, or -1 when the end of the
     *         animation has been reached (the canvas is left unchanged).
     */
    int nextFrame() throws IOException {
        while (true) {
            int block = in.read();
            if (block < 0 || block == 0x3B) {
                return -1; // trailer (or truncated file): end of animation
            }
            switch (block) {
                case 0x21: // extension
                    readExtension();
                    break;
                case 0x2C: // image descriptor
                    readImage();
                    int delay = gceDelayMs;
                    gceDelayMs = 0;
                    gceTransparentIndex = -1;
                    gceDisposal = 0;
                    return delay;
                default:
                    throw new IOException("Corrupt GIF (unexpected block 0x" + Integer.toHexString(block) + "): " + location);
            }
        }
    }

    /** Goes back to the first frame so the animation can loop. */
    void restart() throws IOException {
        close();
        in = open();
        // Skip the header, logical screen descriptor and global colour table
        // that the constructor already read.
        long skip = 6 + 7 + (globalPalette != null ? globalPalette.length * 3L : 0);
        while (skip > 0) {
            long n = in.skip(skip);
            if (n <= 0) {
                if (in.read() < 0) {
                    throw new EOFException();
                }
                n = 1;
            }
            skip -= n;
        }
        java.util.Arrays.fill(canvas, 0);
        pendingDisposal = 0;
        savedArea = null;
        gceDelayMs = 0;
        gceDisposal = 0;
        gceTransparentIndex = -1;
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }

    private void readExtension() throws IOException {
        int label = readByte();
        if (label == 0xF9) { // Graphic Control Extension
            int size = readByte();
            byte[] b = readFully(new byte[size]);
            int packed = b[0] & 0xFF;
            gceDisposal = (packed >> 2) & 7;
            gceDelayMs = ((b[1] & 0xFF) | ((b[2] & 0xFF) << 8)) * 10;
            gceTransparentIndex = (packed & 1) != 0 ? (b[3] & 0xFF) : -1;
            skipSubBlocks();
        } else if (label == 0xFF) { // Application Extension
            int size = readByte();
            byte[] b = readFully(new byte[size]);
            String app = new String(b, "ISO-8859-1");
            if (app.startsWith("NETSCAPE2.0") || app.startsWith("ANIMEXTS1.0")) {
                int sub = readByte();
                while (sub > 0) {
                    byte[] d = readFully(new byte[sub]);
                    if (d.length >= 3 && d[0] == 1) {
                        loopCount = (d[1] & 0xFF) | ((d[2] & 0xFF) << 8);
                    }
                    sub = readByte();
                }
            } else {
                skipSubBlocks();
            }
        } else { // comment, plain text, unknown
            skipSubBlocks();
        }
    }

    private void readImage() throws IOException {
        int left = readShort();
        int top = readShort();
        int w = readShort();
        int h = readShort();
        int packed = readByte();
        boolean interlaced = (packed & 0x40) != 0;
        int[] palette = globalPalette;
        if ((packed & 0x80) != 0) {
            int n = 2 << (packed & 7);
            readPalette(n, localPalette);
            palette = localPalette;
        }
        if (palette == null) {
            throw new IOException("GIF frame has no colour table: " + location);
        }

        // Dispose of the previous frame before drawing this one.
        applyPendingDisposal();

        if (gceDisposal == 3) {
            saveArea(left, top, w, h);
        }

        // Decode this frame's pixel indices.
        if (frameIndices.length < w * h) {
            frameIndices = new byte[w * h];
        }
        decodeLzw(w * h);

        // Composite onto the canvas.
        int transparent = gceTransparentIndex;
        for (int row = 0; row < h; row++) {
            int y = top + row;
            if (y < 0 || y >= height) {
                continue;
            }
            int srcRow = interlaced ? interlacedRow(row, h) : row;
            int src = srcRow * w;
            int dst = y * width;
            for (int col = 0; col < w; col++) {
                int x = left + col;
                if (x < 0 || x >= width) {
                    continue;
                }
                int idx = frameIndices[src + col] & 0xFF;
                if (idx != transparent) {
                    canvas[dst + x] = palette[idx];
                }
            }
        }

        pendingDisposal = gceDisposal;
        prevLeft = left;
        prevTop = top;
        prevWidth = w;
        prevHeight = h;
    }

    /** Maps a row as stored in an interlaced frame to its position on screen. */
    private static int interlacedRow(int stored, int h) {
        // Pass 1: rows 0,8,16..  Pass 2: 4,12,..  Pass 3: 2,6,10,..  Pass 4: 1,3,5,..
        int pass1 = (h + 7) / 8;
        int pass2 = (h + 3) / 8;
        int pass3 = (h + 1) / 4;
        if (stored < pass1) return stored * 8;
        stored -= pass1;
        if (stored < pass2) return stored * 8 + 4;
        stored -= pass2;
        if (stored < pass3) return stored * 4 + 2;
        stored -= pass3;
        return stored * 2 + 1;
    }

    private void applyPendingDisposal() {
        if (pendingDisposal == 2) {
            // Restore to background: treated as transparent, like browsers do.
            fillArea(prevLeft, prevTop, prevWidth, prevHeight, 0);
        } else if (pendingDisposal == 3 && savedArea != null) {
            restoreArea(prevLeft, prevTop, prevWidth, prevHeight);
        }
        pendingDisposal = 0;
    }

    private void fillArea(int left, int top, int w, int h, int value) {
        for (int row = 0; row < h; row++) {
            int y = top + row;
            if (y < 0 || y >= height) continue;
            int x0 = Math.max(0, left), x1 = Math.min(width, left + w);
            if (x1 > x0) {
                java.util.Arrays.fill(canvas, y * width + x0, y * width + x1, value);
            }
        }
    }

    private void saveArea(int left, int top, int w, int h) {
        if (savedArea == null || savedArea.length < w * h) {
            savedArea = new int[w * h];
        }
        for (int row = 0; row < h; row++) {
            int y = top + row;
            for (int col = 0; col < w; col++) {
                int x = left + col;
                savedArea[row * w + col] = (x >= 0 && x < width && y >= 0 && y < height) ? canvas[y * width + x] : 0;
            }
        }
    }

    private void restoreArea(int left, int top, int w, int h) {
        for (int row = 0; row < h; row++) {
            int y = top + row;
            if (y < 0 || y >= height) continue;
            for (int col = 0; col < w; col++) {
                int x = left + col;
                if (x >= 0 && x < width) {
                    canvas[y * width + x] = savedArea[row * w + col];
                }
            }
        }
    }

    /** Reads the LZW sub-blocks for one frame and decodes {@code pixelCount} indices into frameIndices. */
    private void decodeLzw(int pixelCount) throws IOException {
        int minCodeSize = readByte();
        int dataLength = readSubBlocks();

        int clear = 1 << minCodeSize;
        int endOfInfo = clear + 1;
        int codeSize = minCodeSize + 1;
        int codeMask = (1 << codeSize) - 1;
        int available = clear + 2;
        int oldCode = -1;
        int first = 0;

        for (int code = 0; code < clear; code++) {
            prefix[code] = 0;
            suffix[code] = (byte) code;
        }

        int datum = 0, bits = 0, inPos = 0, top = 0, outPos = 0;
        byte[] data = lzwData;

        while (outPos < pixelCount) {
            if (top == 0) {
                if (bits < codeSize) {
                    if (inPos >= dataLength) {
                        break; // truncated data: leave the rest of the frame as is
                    }
                    datum |= (data[inPos++] & 0xFF) << bits;
                    bits += 8;
                    continue;
                }
                int code = datum & codeMask;
                datum >>= codeSize;
                bits -= codeSize;

                if (code == clear) {
                    codeSize = minCodeSize + 1;
                    codeMask = (1 << codeSize) - 1;
                    available = clear + 2;
                    oldCode = -1;
                    continue;
                }
                if (code == endOfInfo) {
                    break;
                }
                if (oldCode == -1) {
                    pixelStack[top++] = suffix[code];
                    oldCode = code;
                    first = code;
                    continue;
                }
                int inCode = code;
                if (code >= available) {
                    pixelStack[top++] = (byte) first;
                    code = oldCode;
                }
                while (code > clear) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = suffix[code] & 0xFF;
                pixelStack[top++] = (byte) first;

                if (available < MAX_CODES) {
                    prefix[available] = (short) oldCode;
                    suffix[available] = (byte) first;
                    available++;
                    if ((available & codeMask) == 0 && available < MAX_CODES) {
                        codeSize++;
                        codeMask += available;
                    }
                }
                oldCode = inCode;
            }
            frameIndices[outPos++] = pixelStack[--top];
        }
        // Anything the stream did not cover is left as index 0.
        while (outPos < pixelCount) {
            frameIndices[outPos++] = 0;
        }
    }

    /** Reads all data sub-blocks into lzwData and returns the total length. */
    private int readSubBlocks() throws IOException {
        int total = 0;
        int size = readByte();
        while (size > 0) {
            if (lzwData.length < total + size) {
                byte[] bigger = new byte[Math.max(lzwData.length * 2, total + size)];
                System.arraycopy(lzwData, 0, bigger, 0, total);
                lzwData = bigger;
            }
            readFully(lzwData, total, size);
            total += size;
            size = readByte();
        }
        return total;
    }

    private void skipSubBlocks() throws IOException {
        int size = readByte();
        while (size > 0) {
            readFully(new byte[size]);
            size = readByte();
        }
    }

    private int[] readPalette(int entries) throws IOException {
        int[] p = new int[256];
        readPalette(entries, p);
        return p;
    }

    private void readPalette(int entries, int[] into) throws IOException {
        byte[] b = readFully(new byte[entries * 3]);
        for (int i = 0; i < entries; i++) {
            into[i] = 0xFF000000 | ((b[i * 3] & 0xFF) << 16) | ((b[i * 3 + 1] & 0xFF) << 8) | (b[i * 3 + 2] & 0xFF);
        }
        for (int i = entries; i < 256; i++) {
            into[i] = 0xFF000000;
        }
    }

    private int readByte() throws IOException {
        int b = in.read();
        if (b < 0) {
            throw new EOFException("Truncated GIF: " + location);
        }
        return b;
    }

    private int readShort() throws IOException {
        int lo = readByte();
        int hi = readByte();
        return lo | (hi << 8);
    }

    private byte[] readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
        return b;
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int n = in.read(b, off, len);
            if (n < 0) {
                throw new EOFException("Truncated GIF: " + location);
            }
            off += n;
            len -= n;
        }
    }
}
