package View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_Help {

    Timer timer;

    public CountDown_Help(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            timer.cancel();
        }
    }
}
