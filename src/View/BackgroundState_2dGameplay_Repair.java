package View;

import Model.GameData;

import javax.swing.*;
import java.awt.*;

import static View.MyWindow.repairButton;

public class BackgroundState_2dGameplay_Repair implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("repair.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        GameData.loaded = true;
        repairButton.setEnabled(false);
        canvas.backgroundState = new BackgroundState_2dGameplay_Offense();
    }
}
