package View;

import Controller.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackgroundState_StartMenu_Dashboard implements BackgroundState {

    ImageIcon icon;
    Image image;

    public BufferedImage img; {
        try {
            img = ImageIO.read(getClass().getResource("title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BackgroundState_StartMenu_Dashboard() {
        icon = new ImageIcon(this.getClass().getResource("onStart.gif"));
        image = icon.getImage();
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
        g2.drawImage(img,0,0,null);
        for (var fig : Main.gameData.friendObjects) {
            fig.render(g2);
        }
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_Transition_Offense();
    }
}
