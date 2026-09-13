package View;

import javax.imageio.ImageIO;
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
                IMAGES[lightSaber] = ImageIO.read(HealthOverlay.class.getResource("health_" + lightSaber + ".png"));
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return IMAGES[lightSaber];
    }
}
