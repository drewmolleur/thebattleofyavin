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
            repair = true;
            new CountDown_Repair(1);
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
            else fig.update();
        }
        fixedObjects.removeAll(remove);

        remove.clear();
        for (var fig : friendObjects) {
            if (fig.done) remove.add(fig);
            else fig.update();
        }
        friendObjects.removeAll(remove);

        remove.clear();
        for (var fig : enemyObjects) {
            if (fig.done) remove.add(fig);
            else fig.update();
        }
        enemyObjects.removeAll(remove);
    }
    public void clear() {
        fixedObjects.clear();
        friendObjects.clear();
        enemyObjects.clear();
    }
}
