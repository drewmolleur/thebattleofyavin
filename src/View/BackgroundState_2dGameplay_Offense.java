package View;

import Controller.Main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Controller.Main.*;
import static View.MyWindow.*;
import static View.MyWindow.useTheForceButton;

public class BackgroundState_2dGameplay_Offense implements BackgroundState {

    AnimatedImage image = new AnimatedImage(getClass().getResource("2dGameplay.gif"));

    public static BufferedImage health;

    @Override
    public void render(Graphics2D g2) {
        if (Main.lightSaber >= 1 && Main.lightSaber <= 6) {
            health = HealthOverlay.forLightSaber(Main.lightSaber);
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
        image.draw(g2, 0, 0);
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
