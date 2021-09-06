package View;

import Controller.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static Controller.Main.*;
import static View.MyWindow.*;
import static View.MyWindow.useTheForceButton;

public class BackgroundState_2dGameplay_Offense implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("2dGameplay.gif"));
    Image image = icon.getImage();

    public static BufferedImage health;

    @Override
    public void render(Graphics2D g2) {
        if (Main.lightSaber == 6)
            try {
                health = ImageIO.read(getClass().getResource("health_6.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        else if (Main.lightSaber == 5) {
            try {
                health = ImageIO.read(getClass().getResource("health_5.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Main.lightSaber == 4) {
            try {
                health = ImageIO.read(getClass().getResource("health_4.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Main.lightSaber == 3) {
            try {
                health = ImageIO.read(getClass().getResource("health_3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Main.lightSaber == 2) {
            try {
                health = ImageIO.read(getClass().getResource("health_2.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Main.lightSaber == 1) {
            try {
                health = ImageIO.read(getClass().getResource("health_1.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Main.lightSaber == 0) {
            themeSong.stop();
            onStart.start();
            gameData.defense = true;
            quitButton.setVisible(true);
            quitButton.setEnabled(true);
            startButton.setVisible(false);
            helpButton.setVisible(false);
            repairButton.setVisible(false);
            backUpButton.setVisible(false);
            useTheForceButton.setVisible(false);
            win.canvas.backgroundState = new BackgroundState_Outcome_YouLose();
        }
        g2.drawImage(image,0,0,null);
        for (var fig : Main.gameData.fixedObjects) {
            fig.render(g2);
        }
        for (var fig : Main.gameData.friendObjects) {
            fig.render(g2);
        }
        for (var fig : Main.gameData.enemyObjects) {
            fig.render(g2);
        }
        g2.drawImage(health,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_Transition_Offense_Completed();
    }
}
