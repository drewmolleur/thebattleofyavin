package View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_UseTheForce {

    public static Timer timer;

    public static int count = 4;

    public CountDown_UseTheForce(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*333);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            if(count == 3) {
                System.out.println(count + " attempts remaining..." + System.lineSeparator());
            } else if(count == 2) {
                System.out.println(count + " attempts remaining..." + System.lineSeparator());
            } else if(count == 1) {
                System.out.println(count + " attempts remaining..." + System.lineSeparator());
            }
            MyWindow.useTheForceButton.setEnabled(false);
            timer.cancel();
            new CountDown_DisableButton(1);
        }
    }
}
