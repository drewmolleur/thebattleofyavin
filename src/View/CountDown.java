package View;

import Controller.Main;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown {

    Timer timer;

    public CountDown(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            //System.out.println("Countdown Initiated");
            Main.win.canvas.backgroundState.goNext(Main.win.canvas);
            timer.cancel();
        }
    }
}
