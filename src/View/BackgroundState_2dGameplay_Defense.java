package View;

import Controller.Main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Controller.Main.*;
import static Controller.Main.win;
import static View.MyWindow.*;
import static View.MyWindow.useTheForceButton;

public class BackgroundState_2dGameplay_Defense implements BackgroundState {

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
        canvas.backgroundState = new BackgroundState_Transition_BackUp();
        System.out.println( System.lineSeparator() +
                "Stage Two COMPLETE" + System.lineSeparator() + System.lineSeparator() +
                "Stage Three: THE MISSION" +  System.lineSeparator() +
                "Use The Force in under 3 attempts to destroy the enemy space station." +  System.lineSeparator());
    }
}
