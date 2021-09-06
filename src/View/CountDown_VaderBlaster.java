package View;

import Controller.Main;
import Model.Vader.Vader;
import Model.VaderBlaster.VaderBlaster;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_VaderBlaster {

    Timer timer;

    public CountDown_VaderBlaster(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*500);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            Main.gameData.enemyObjects.add(new VaderBlaster(0,0, (Vader)Main.gameData.enemyObjects.get(1)));
            timer.cancel();
        }
    }
}
