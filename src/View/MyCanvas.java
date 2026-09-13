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

    // Triple buffer. The game thread draws into a buffer that is neither the
    // latest finished frame nor the one Swing is painting right now, so the
    // two threads never wait for each other and never touch the same buffer.
    private final BufferedImage[] buffers = new BufferedImage[3];
    private int latest = -1;    // newest finished frame, -1 until the first render
    private int painting = -1;  // buffer the EDT is currently drawing, -1 if none
    private final Object bufferLock = new Object();

    public MyCanvas() {
        setOpaque(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
    }

    // Called from the game loop: draw the next frame off-screen, then ask Swing to show it.
    public void render() {
        long start = System.nanoTime();
        int target;
        synchronized (bufferLock) {
            target = 0;
            while (target == latest || target == painting) {
                target++;
            }
        }
        if (buffers[target] == null) {
            buffers[target] = createBuffer(GAME_WIDTH, GAME_HEIGHT);
        }

        Graphics2D g2OffScreen = buffers[target].createGraphics();
        try {
            g2OffScreen.setColor(Color.BLACK);
            g2OffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            backgroundState.render(g2OffScreen);
        } finally {
            g2OffScreen.dispose();
        }

        synchronized (bufferLock) {
            latest = target;
        }
        long nanos = System.nanoTime() - start;
        FrameStats.renderDone(nanos);
        if (nanos > 150_000_000L) {
            FrameStats.stall("drawing one frame took " + nanos / 1_000_000 + " ms in " + backgroundState.getClass().getSimpleName());
        }
        repaint();
    }

    // Every paint, whether requested by render() or by Swing itself (button
    // changes, focus changes, window events), shows the last finished frame.
    @Override
    protected void paintComponent(Graphics g) {
        long start = System.nanoTime();
        BufferedImage frame;
        synchronized (bufferLock) {
            if (latest < 0) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                return;
            }
            painting = latest;
            frame = buffers[painting];
        }
        try {
            Rectangle bounds = getGameBounds();
            if (bounds.width < getWidth() || bounds.height < getHeight()) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight()); // letterbox bars
            }
            g.drawImage(frame, bounds.x, bounds.y, bounds.width, bounds.height, null);
        } finally {
            synchronized (bufferLock) {
                painting = -1;
            }
        }
        long nanos = System.nanoTime() - start;
        FrameStats.paintDone(nanos);
        if (nanos > 150_000_000L) {
            FrameStats.stall("putting a frame on screen took " + nanos / 1_000_000 + " ms");
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
