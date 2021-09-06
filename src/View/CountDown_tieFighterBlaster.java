package View;

import Controller.Main;
import Model.Vader.Vader;
import Model.VaderBlaster.VaderBlaster;
import Model.tieFighter.tieFighter;
import Model.tieFighterBlaster.tieFighterBlaster;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_tieFighterBlaster {

    Timer timer;

    public CountDown_tieFighterBlaster(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*500);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            Main.gameData.enemyObjects.add(new tieFighterBlaster(0,0, (tieFighter)Main.gameData.enemyObjects.get(0)));
            Main.gameData.enemyObjects.add(new tieFighterBlaster(0,0, (tieFighter)Main.gameData.enemyObjects.get(2)));
            timer.cancel();
        }
    }
}
