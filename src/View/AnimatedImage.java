package View;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;

/**
 * A flicker-free wrapper around an animated GIF loaded through the AWT Toolkit.
 *
 * The Toolkit decodes an animated GIF on a background thread and writes each
 * new frame straight into the image's pixel buffer, row by row. Drawing that
 * image with a {@code null} observer copies whatever happens to be in the
 * buffer at that instant: a half-decoded frame (horizontal streaks) or a frame
 * whose area has just been wiped by the GIF's disposal step (black frames).
 *
 * This class registers itself as an {@link ImageObserver} and copies the
 * buffer into its own {@link BufferedImage} only when the decoder reports a
 * complete frame ({@code FRAMEBITS} / {@code ALLBITS}). {@link #draw} always
 * paints that last complete frame, so callers never see a partial one.
 */
public class AnimatedImage implements ImageObserver {

    /** How long to wait for the first complete frame before giving up. */
    private static final long FIRST_FRAME_TIMEOUT_MS = 5000;

    private final Image source;
    private volatile BufferedImage currentFrame;
    private BufferedImage spareFrame;
    private boolean finished;

    /**
     * Loads the image at {@code location} and, like {@code ImageIcon}, blocks
     * until the first frame is available so the first draw is not empty.
     */
    public AnimatedImage(URL location) {
        this(Toolkit.getDefaultToolkit().getImage(location));
    }

    public AnimatedImage(Image source) {
        this.source = source;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // Start decoding and subscribe to every complete-frame notification.
        boolean alreadyComplete = toolkit.prepareImage(source, -1, -1, this);
        if (alreadyComplete) {
            // A fully loaded static image: the buffer will never change again.
            captureFrame();
            return;
        }
        waitForFirstFrame();
    }

    /** Draws the most recent complete frame at the given position. */
    public void draw(Graphics2D g2, int x, int y) {
        BufferedImage frame = currentFrame;
        if (frame != null) {
            g2.drawImage(frame, x, y, null);
        }
    }

    /**
     * Called by the Toolkit on the image producer's thread. A FRAMEBITS or
     * ALLBITS notification is sent right after a full frame has been decoded
     * and before the decoder starts modifying the buffer for the next one, so
     * that is the only safe moment to copy the buffer.
     */
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & (ERROR | ABORT)) != 0) {
            markFinished();
            return false;
        }
        if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
            captureFrame();
        }
        if ((infoflags & ALLBITS) != 0) {
            markFinished();
            return false;
        }
        return true;
    }

    private synchronized void captureFrame() {
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        if (w <= 0 || h <= 0) {
            return;
        }
        if (spareFrame == null || spareFrame.getWidth() != w || spareFrame.getHeight() != h) {
            spareFrame = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g = spareFrame.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        // Swap: the frame just filled becomes the one to show and the old one
        // becomes the spare. Readers only ever see a fully written frame.
        BufferedImage previous = currentFrame;
        currentFrame = spareFrame;
        spareFrame = previous;
        notifyAll();
    }

    private synchronized void markFinished() {
        finished = true;
        notifyAll();
    }

    private synchronized void waitForFirstFrame() {
        long deadline = System.currentTimeMillis() + FIRST_FRAME_TIMEOUT_MS;
        while (currentFrame == null && !finished) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                System.err.println("AnimatedImage: timed out waiting for the first frame of " + source);
                return;
            }
            try {
                wait(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
