package View;

import Controller.Main;

import java.awt.*;

import static Controller.Main.addTieFighterWithListener;
import static Controller.Main.addVaderWithListener;

public class BackgroundState_Transition_Offense_Completed implements BackgroundState {

    AnimatedImage image = AnimatedImage.load(getClass().getResource("completed.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        MyWindow.repairButton.setEnabled(false);
        addTieFighterWithListener(400,700);
        addVaderWithListener(700,700);
        addTieFighterWithListener(1000,700);
        Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER).location.x=MyCanvas.GAME_WIDTH/2;
        Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER).location.y=250;
        Main.win.canvas.backgroundState = new BackgroundState_2dGameplay_Defense();
        new CountDown_VaderBlaster(5);
        new CountDown_tieFighterBlaster(4);
        new CountDown_VaderBlaster(8);
        new CountDown_tieFighterBlaster(7);
        new CountDown_VaderBlaster(11);
        new CountDown_tieFighterBlaster(10);
        new CountDown_VaderBlaster(14);
        new CountDown_tieFighterBlaster(13);
        new CountDown_VaderBlaster(17);
        new CountDown_tieFighterBlaster(16);
        System.out.println( System.lineSeparator() +
                "Stage Two: DEFENSE" +  System.lineSeparator() +
                "Evade Darth Vader for 20 seconds to advance to the next stage." );
    }
}
