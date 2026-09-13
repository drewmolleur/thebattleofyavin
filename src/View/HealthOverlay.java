package View;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The health bar overlays (health_1.png .. health_6.png), decoded once and
 * cached. The gameplay states used to re-read the PNG from disk on every
 * frame, which cost several milliseconds per frame and made the frame rate
 * uneven.
 */
public final class HealthOverlay {

    private static final BufferedImage[] IMAGES = new BufferedImage[7];

    private HealthOverlay() {
    }

    /** Returns the overlay for the given light-saber count (1..6), or null if there is none. */
    public static synchronized BufferedImage forLightSaber(int lightSaber) {
        if (lightSaber < 1 || lightSaber > 6) {
            return null;
        }
        if (IMAGES[lightSaber] == null) {
            try {
                BufferedImage png = ImageIO.read(HealthOverlay.class.getResource("health_" + lightSaber + ".png"));
                IMAGES[lightSaber] = toPremultiplied(png);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return IMAGES[lightSaber];
    }

    /**
     * Blending a full-screen translucent image onto the frame every frame is
     * far cheaper when the image stores premultiplied alpha, so convert once.
     */
    private static BufferedImage toPremultiplied(BufferedImage png) {
        BufferedImage img = new BufferedImage(png.getWidth(), png.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = img.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(png, 0, 0, null);
        } finally {
            g.dispose();
        }
        return img;
    }
}
