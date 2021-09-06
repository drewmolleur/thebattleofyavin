package View;

import Controller.Main;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown_ShowButtons {

    public static Timer timer;

    public CountDown_ShowButtons(int seconds) {
        timer = new Timer();
        timer.schedule(new CountDownTask(), seconds*1000);
    }

    class CountDownTask extends TimerTask {
        public void run() {
            MyWindow.helpButton.setVisible(false);
            MyWindow.helpButton.setEnabled(true);
            MyWindow.startButton.setVisible(false);
            MyWindow.startButton.setEnabled(true);
            MyWindow.quitButton.setVisible(true);
            MyWindow.quitButton.setEnabled((true));
            timer.cancel();
        }
    }
}
