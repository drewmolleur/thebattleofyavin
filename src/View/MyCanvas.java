package View;

import javax.swing.*;
import java.awt.*;

public class MyCanvas extends JPanel {

    // canvas dimensions
    public static int width, height;

    // initial background state
    public BackgroundState backgroundState = new BackgroundState_StartMenu_Intro();
    public HealthState healthState = new HealthState_xWing_5();

    // draw to screen
    public void render() {
        width = getSize().width;
        height = getSize().height;

        // off-screen double buffer image
        Image doubleBufferImage = createImage(width, height);
        if (doubleBufferImage == null) {
            System.out.println("Critical error: doubleBufferImage is null");
            System.exit(1);
        }

        //off-screen rendering
        Graphics2D g2OffScreen = (Graphics2D) doubleBufferImage.getGraphics();
        if (g2OffScreen == null) {
            System.out.println("Critical error: g2OffScreen is null");
            System.exit(1);
        }

        //initialize the image buffer
        g2OffScreen.setBackground(Color.BLACK);
        g2OffScreen.clearRect(0, 0, width, height);
        backgroundState.render(g2OffScreen);

        // use active rendering to put the buffer image on screen
        Graphics gOnScreen;
        gOnScreen = this.getGraphics();

        if (gOnScreen != null) {
            //copy offScreen image to onScreen
            gOnScreen.drawImage(doubleBufferImage, 0, 0, null);
        }

        // sync the display on the some systems
        Toolkit.getDefaultToolkit().sync();
        if (gOnScreen != null) {
            gOnScreen.dispose();
        }
        paint(g2OffScreen);
    }

    public void paint(Graphics g) {
        final Graphics2D g2d;
        g2d = (Graphics2D) g;
        g2d.drawImage(null, null, 0, 0);
    }
}