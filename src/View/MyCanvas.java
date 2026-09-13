package View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends JPanel {

    // canvas dimensions
    public static int width, height;

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
    }

    // Called from the game loop: draw the next frame off-screen, then ask Swing to show it.
    public void render() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return; // not laid out yet
        }
        width = w;
        height = h;

        if (drawBuffer == null || drawBuffer.getWidth() != w || drawBuffer.getHeight() != h) {
            drawBuffer = createBuffer(w, h);
        }

        Graphics2D g2OffScreen = drawBuffer.createGraphics();
        try {
            g2OffScreen.setColor(Color.BLACK);
            g2OffScreen.fillRect(0, 0, w, h);
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
        synchronized (bufferLock) {
            if (showBuffer != null) {
                g.drawImage(showBuffer, 0, 0, null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private BufferedImage createBuffer(int w, int h) {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            return gc.createCompatibleImage(w, h, Transparency.OPAQUE);
        }
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }
}
