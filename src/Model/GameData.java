package Model;

import Controller.Main;
import View.*;

import java.util.ArrayList;

import static Controller.Main.*;
import static View.MyWindow.*;
import static View.MyWindow.useTheForceButton;

public class GameData {

    public ArrayList<GameFigure> fixedObjects = new ArrayList<>(); // xWing
    public ArrayList<GameFigure> friendObjects = new ArrayList<>(); // text
    public ArrayList<GameFigure> enemyObjects = new ArrayList<>(); // tieFighter, vader

    public static boolean loaded = false;
    public static boolean defense = false;
    public static boolean repair = false;

    public void update() {

        // Narrative Engine
        int enemiesLeft = enemyObjects.size();
        // From Offense to Defense
        if(enemiesLeft == 0 && defense == false && loaded == true) {
            defense = true;
            offenseDone.start();
            System.out.println(System.lineSeparator() + "Stage One COMPLETE");
            win.canvas.backgroundState.goNext(win.canvas);
            new CountDown(7);
        }
        // Back-Up Button
        if(enemiesLeft == 3 && defense == true) {
            defense = false;
            new CountDown_Defense(10);
        }

        if(lightSaber == 3 && defense == false) {
            if (!repair) {           // prompt once, not on every update
                repair = true;
                new CountDown_Repair(1);
            }
        } else {
            repair = false;
        }

        if(CountDown_UseTheForce.count == 0 ){
            themeSong.stop();
            onStart.start();
            quitButton.setVisible(true);
            quitButton.setEnabled(true);
            startButton.setVisible(false);
            helpButton.setVisible(false);
            repairButton.setVisible(false);
            backUpButton.setVisible(false);
            useTheForceButton.setVisible(false);
            System.out.println(System.lineSeparator() + "YOU LOSE. TRY AGAIN!");
            win.canvas.backgroundState = new BackgroundState_Outcome_YouLose();
            CountDown_DisableButton.timer.cancel();
            CountDown_UseTheForce.timer.cancel();
            MyWindow.useTheForceButton.setEnabled(false);
        }
        // Remove Done Figures
        ArrayList<GameFigure> remove = new ArrayList<>();
        for (var fig : fixedObjects) {
            if (fig.done) remove.add(fig);
            else { fig.rememberPosition(); fig.update(); }
        }
        fixedObjects.removeAll(remove);

        remove.clear();
        for (var fig : friendObjects) {
            if (fig.done) remove.add(fig);
            else { fig.rememberPosition(); fig.update(); }
        }
        friendObjects.removeAll(remove);

        remove.clear();
        for (var fig : enemyObjects) {
            if (fig.done) remove.add(fig);
            else { fig.rememberPosition(); fig.update(); }
        }
        enemyObjects.removeAll(remove);
    }

    /**
     * Updates happen UPDATES_PER_SECOND times a second but frames are drawn
     * more often. Before drawing, move every figure to where it was
     * {@code alpha} (0..1) of the way from its previous update position to
     * its current one, so motion looks smooth; restore afterwards.
     */
    public void interpolatePositions(float alpha) {
        for (var fig : fixedObjects) fig.interpolatePosition(alpha);
        for (var fig : friendObjects) fig.interpolatePosition(alpha);
        for (var fig : enemyObjects) fig.interpolatePosition(alpha);
    }

    public void restorePositions() {
        for (var fig : fixedObjects) fig.restorePosition();
        for (var fig : friendObjects) fig.restorePosition();
        for (var fig : enemyObjects) fig.restorePosition();
    }
    public void clear() {
        fixedObjects.clear();
        friendObjects.clear();
        enemyObjects.clear();
    }
}
