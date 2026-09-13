package View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends JPanel {

    // The game draws everything in this fixed coordinate space, which is the
    // size of the background artwork. paintComponent() scales the finished
    // frame to whatever size the panel actually has on screen.
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 816;

    // canvas dimensions in game coordinates
    public static int width = GAME_WIDTH, height = GAME_HEIGHT;

    // initial background state
    public BackgroundState backgroundState = new BackgroundState_StartMenu_Intro();
    public HealthState healthState = new HealthState_xWing_5();

    // Double buffer: the game thread draws into drawBuffer, Swing paints showBuffer.
    private BufferedImage drawBuffer;
    private BufferedImage showBuffer;
    private final Object bufferLock = new Object();

    public MyCanvas() {
        setOpaque(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
    }

    // Called from the game loop: draw the next frame off-screen, then ask Swing to show it.
    public void render() {
        if (drawBuffer == null) {
            drawBuffer = createBuffer(GAME_WIDTH, GAME_HEIGHT);
        }

        Graphics2D g2OffScreen = drawBuffer.createGraphics();
        try {
            g2OffScreen.setColor(Color.BLACK);
            g2OffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            backgroundState.render(g2OffScreen);
        } finally {
            g2OffScreen.dispose();
        }

        // Publish the finished frame. paintComponent() holds the same lock while
        // copying showBuffer to the screen, so it never sees a half-drawn frame.
        synchronized (bufferLock) {
            BufferedImage finished = drawBuffer;
            drawBuffer = showBuffer;
            showBuffer = finished;
        }
        repaint();
    }

    // Every paint, whether requested by render() or by Swing itself (button
    // changes, focus changes, window events), shows the last finished frame.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Rectangle bounds = getGameBounds();
        synchronized (bufferLock) {
            if (showBuffer == null) {
                return;
            }
            if (bounds.width == GAME_WIDTH && bounds.height == GAME_HEIGHT) {
                g2.drawImage(showBuffer, bounds.x, bounds.y, null);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(showBuffer, bounds.x, bounds.y, bounds.width, bounds.height, null);
            }
        }
    }

    /**
     * Where the game image sits inside the panel: scaled to fit the panel with
     * its aspect ratio kept, and centred. On a screen at least 1920 wide this
     * is simply the full-size image at the top-left corner.
     */
    public Rectangle getGameBounds() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        double scale = Math.min(panelWidth / (double) GAME_WIDTH, panelHeight / (double) GAME_HEIGHT);
        if (scale <= 0) {
            return new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }
        int w = (int) Math.round(GAME_WIDTH * scale);
        int h = (int) Math.round(GAME_HEIGHT * scale);
        return new Rectangle((panelWidth - w) / 2, (panelHeight - h) / 2, w, h);
    }

    /** Converts a mouse position on the panel into game coordinates. */
    public Point toGamePoint(int panelX, int panelY) {
        Rectangle bounds = getGameBounds();
        int x = (int) Math.round((panelX - bounds.x) * (double) GAME_WIDTH / bounds.width);
        int y = (int) Math.round((panelY - bounds.y) * (double) GAME_HEIGHT / bounds.height);
        return new Point(x, y);
    }

    private BufferedImage createBuffer(int w, int h) {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            return gc.createCompatibleImage(w, h, Transparency.OPAQUE);
        }
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }
}
