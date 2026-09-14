package View;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Plays an animated GIF background smoothly and cheaply.
 *
 * Java's built-in Toolkit GIF support is a poor fit for the game's large
 * video-like GIFs: it draws half-decoded frames (flicker), it sleeps for the
 * frame delay only after spending the decode time (so playback runs slow),
 * and it buffers the entire file in memory so that it can loop (hundreds of
 * megabytes per background, with garbage-collection stalls whenever a new
 * one starts).
 *
 * This class instead streams frames through {@link GifDecoder} on its own
 * thread, decoding each frame ahead of the moment it is due, and publishes
 * only complete frames. {@link #draw} always paints the latest complete
 * frame. Memory use is a few screen-sized buffers regardless of file size.
 * The decoding thread stops itself when the image has not been drawn for a
 * while (the scene moved on) and resumes on the next draw.
 */
public class AnimatedImage {

    /** Frames with a tiny or missing delay are shown for this long, as browsers do. */
    private static final int MIN_DELAY_MS = 20;
    private static final int DEFAULT_DELAY_MS = 100;
    /** Stop decoding when nobody has drawn this image for this long. */
    private static final long IDLE_STOP_NANOS = 2_000_000_000L;
    /**
     * Extra display time per 100 KB of compressed frame data, in milliseconds.
     *
     * Java's old decoder showed each frame for its stored delay *plus* the
     * time it took to decode it, which grows with the amount of data in the
     * frame. So detailed footage played at roughly half its stored rate while
     * near-black frames (a fade-in) played at almost full rate, and the game's
     * countdowns and clip lengths were tuned to that pacing. This reproduces
     * it: a typical 300 KB frame with a 30 ms delay is shown for about 60 ms.
     * Set -Dyavin.gif.slowdown=0 to play the files at their stored rate.
     */
    private static final double SLOWDOWN_MS_PER_100KB = Double.parseDouble(System.getProperty("yavin.gif.slowdown", "10"));
    /** Decoding a frame slower than this is reported as a stall. */
    private static final long STALL_REPORT_NANOS = 150_000_000L;

    private static final Map<String, WeakReference<AnimatedImage>> CACHE = new HashMap<>();

    /**
     * Returns the player for a GIF, sharing one between scenes that use the
     * same file while any of them is alive, so the footage carries on where
     * it was instead of restarting at the cut. Unused players are garbage
     * collected normally.
     */
    public static synchronized AnimatedImage load(URL location) {
        String key = String.valueOf(location);
        WeakReference<AnimatedImage> ref = CACHE.get(key);
        AnimatedImage image = ref != null ? ref.get() : null;
        if (image == null) {
            image = new AnimatedImage(location);
            CACHE.put(key, new WeakReference<>(image));
        }
        return image;
    }

    private final URL location;
    private GifDecoder decoder;
    private int loopsRemaining;      // -1 = forever
    private volatile boolean finished; // animation ended (or failed): hold the last frame

    // Triple buffer, same scheme as MyCanvas: the decoder writes into a buffer
    // that is neither the one currently shown nor the one being drawn.
    private final BufferedImage[] buffers = new BufferedImage[3];
    private int shown = -1;
    private int drawing = -1;
    private final Object lock = new Object();

    private Thread player;
    private volatile long lastDrawNanos = System.nanoTime();

    public AnimatedImage(URL location) {
        this.location = location;
        try {
            decoder = new GifDecoder(location);
            // Decode the first frame right away so the first draw is not empty.
            int delay = decoder.nextFrame();
            // The loop count is read from an extension that precedes the first frame.
            loopsRemaining = decoder.loopCount < 0 ? 0 : (decoder.loopCount == 0 ? -1 : decoder.loopCount - 1);
            if (delay < 0) {
                finished = true;
            } else {
                pendingDelay = displayTime(delay, decoder.lastFrameDataBytes);
                publish(copyCanvas(freeBuffer()));
            }
        } catch (IOException e) {
            System.err.println("AnimatedImage: cannot play " + location + ": " + e.getMessage());
            finished = true;
        }
    }

    /** Draws the most recent complete frame at the given position. */
    public void draw(Graphics2D g2, int x, int y) {
        lastDrawNanos = System.nanoTime();
        ensurePlaying();
        BufferedImage frame;
        synchronized (lock) {
            if (shown < 0) {
                return;
            }
            drawing = shown;
            frame = buffers[drawing];
        }
        try {
            g2.drawImage(frame, x, y, null);
        } finally {
            synchronized (lock) {
                drawing = -1;
            }
        }
    }

    private synchronized void ensurePlaying() {
        if (finished || (player != null && player.isAlive())) {
            return;
        }
        player = new Thread(this::play, "gif-player " + location.getFile());
        player.setDaemon(true);
        player.start();
    }

    /** Decoder thread: decode the next frame, wait until it is due, publish, repeat. */
    private void play() {
        long due = System.nanoTime();
        int delay = pendingDelay;
        try {
            while (true) {
                if (System.nanoTime() - lastDrawNanos > IDLE_STOP_NANOS) {
                    return; // nobody is looking; draw() restarts us
                }
                // Decode the frame that follows the one currently shown.
                long decodeStart = System.nanoTime();
                int nextDelay = decoder.nextFrame();
                if (nextDelay < 0) {
                    if (loopsRemaining == 0) {
                        finished = true;
                        return;
                    }
                    if (loopsRemaining > 0) {
                        loopsRemaining--;
                    }
                    decoder.restart();
                    nextDelay = decoder.nextFrame();
                    if (nextDelay < 0) {
                        finished = true;
                        return;
                    }
                }
                int target = copyCanvas(freeBuffer());
                long decodeNanos = System.nanoTime() - decodeStart;
                if (decodeNanos > STALL_REPORT_NANOS) {
                    FrameStats.stall("decoding one frame of " + name() + " took " + decodeNanos / 1_000_000 + " ms");
                }

                // Show it once the frame before it has been on screen for its delay.
                due += (long) delay * 1_000_000L;
                long now = System.nanoTime();
                if (due < now - 500_000_000L) {
                    due = now; // fell far behind (decoding too slow): don't try to catch up
                }
                long wait = due - now;
                if (wait > 0) {
                    Thread.sleep(wait / 1_000_000L, (int) (wait % 1_000_000L));
                }
                publish(target);
                FrameStats.backgroundFrameDone();
                delay = displayTime(nextDelay, decoder.lastFrameDataBytes);
                pendingDelay = delay;
            }
        } catch (IOException e) {
            System.err.println("AnimatedImage: playback of " + location + " stopped: " + e.getMessage());
            finished = true;
        } catch (InterruptedException e) {
            // stopping
        }
    }

    /** Delay of the frame currently shown; the next frame is due this long after it appeared. */
    private volatile int pendingDelay = DEFAULT_DELAY_MS;

    private int freeBuffer() {
        synchronized (lock) {
            int i = 0;
            while (i == shown || i == drawing) {
                i++;
            }
            if (buffers[i] == null) {
                buffers[i] = new BufferedImage(decoder.width, decoder.height, BufferedImage.TYPE_INT_RGB);
            }
            return i;
        }
    }

    /** Copies the decoder's canvas into buffer i (transparent pixels come out black). */
    private int copyCanvas(int i) {
        int[] pixels = ((DataBufferInt) buffers[i].getRaster().getDataBuffer()).getData();
        System.arraycopy(decoder.canvas, 0, pixels, 0, pixels.length);
        return i;
    }

    private void publish(int i) {
        synchronized (lock) {
            shown = i;
        }
    }

    /** How long to show a frame: its stored delay plus the emulated decode time. */
    static int displayTime(int delayMs, int frameDataBytes) {
        int ms = delayMs < MIN_DELAY_MS ? DEFAULT_DELAY_MS : delayMs;
        return ms + (int) Math.round(frameDataBytes / 102400.0 * SLOWDOWN_MS_PER_100KB);
    }

    private String name() {
        String f = location.getFile();
        return f.substring(f.lastIndexOf('/') + 1);
    }
}
