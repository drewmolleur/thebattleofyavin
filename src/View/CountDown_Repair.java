package View;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_Repair {

    Timer timer;

    public CountDown_Repair(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            System.out.println(System.lineSeparator() + "Repair Your X-Wing Now!");
            MyWindow.repairButton.setEnabled(true);
            timer.cancel();
        }
    }
}
