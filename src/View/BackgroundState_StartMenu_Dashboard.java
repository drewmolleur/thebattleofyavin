package View;

import Controller.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackgroundState_StartMenu_Dashboard implements BackgroundState {

    AnimatedImage image;

    public BufferedImage img; {
        try {
            img = ImageIO.read(getClass().getResource("title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BackgroundState_StartMenu_Dashboard() {
        image = new AnimatedImage(getClass().getResource("onStart.gif"));
    }

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
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
