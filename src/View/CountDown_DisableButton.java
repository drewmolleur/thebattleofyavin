package View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_DisableButton {

    public static Timer timer;

    public CountDown_DisableButton(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            CountDown_UseTheForce.count--;
            MyWindow.useTheForceButton.setEnabled(true);
            timer.cancel();
            new CountDown_UseTheForce(1);
        }
    }
}
