package View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_Defense {

    Timer timer;

    public CountDown_Defense(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            System.out.println(System.lineSeparator() + "Call For Back-Up Below!");
            MyWindow.backUpButton.setEnabled(true);
            timer.cancel();
        }
    }
}
