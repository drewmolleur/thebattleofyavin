package View;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static View.MyWindow.helpButton;
import static View.MyWindow.quitButton;

public class BackgroundState_StartMenu_Intro implements BackgroundState {

    AnimatedImage image;

    public BufferedImage img;
    {
        try {
            img = ImageIO.read(getClass().getResource("title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BackgroundState_StartMenu_Intro() {
        image = AnimatedImage.load(getClass().getResource("onStart.gif"));
    }

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        quitButton.setEnabled(true);
        helpButton.setEnabled(true);
        canvas.backgroundState = new BackgroundState_StartMenu_Dashboard();
    }
}
