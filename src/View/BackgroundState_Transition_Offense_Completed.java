package View;

import Controller.Main;

import javax.swing.*;
import java.awt.*;

import static Controller.Main.addTieFighterWithListener;
import static Controller.Main.addVaderWithListener;

public class BackgroundState_Transition_Offense_Completed implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("completed.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        MyWindow.repairButton.setEnabled(false);
        addTieFighterWithListener(400,700);
        addVaderWithListener(700,700);
        addTieFighterWithListener(1000,700);
        Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER).location.x=Main.win.canvas.getWidth()/2;
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
