package View;

import Model.GameData;

import javax.swing.*;
import java.awt.*;

public class BackgroundState_Transition_Offense implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("begin.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        GameData.loaded = true;
        canvas.backgroundState = new BackgroundState_2dGameplay_Offense();
    }
}
