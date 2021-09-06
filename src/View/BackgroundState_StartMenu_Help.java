package View;

import Controller.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackgroundState_StartMenu_Help implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("dashboard.gif"));
    Image image = icon.getImage();

    public static BufferedImage help;
    {
        try {
            help = ImageIO.read(getClass().getResource("help_Overlay.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
        g2.drawImage(help,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_Transition_Offense();
    }
}
