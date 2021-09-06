package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static View.MyWindow.helpButton;
import static View.MyWindow.quitButton;

public class BackgroundState_StartMenu_Intro implements BackgroundState {

    ImageIcon icon;
    Image image;

    public BufferedImage img;
    {
        try {
            img = ImageIO.read(getClass().getResource("title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BackgroundState_StartMenu_Intro() {
        icon = new ImageIcon(this.getClass().getResource("onStart.gif"));
        image = icon.getImage();
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        quitButton.setEnabled(true);
        helpButton.setEnabled(true);
        canvas.backgroundState = new BackgroundState_StartMenu_Dashboard();
    }
}
