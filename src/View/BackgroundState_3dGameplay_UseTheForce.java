package View;

import javax.swing.*;
import java.awt.*;

import static View.MyWindow.*;

public class BackgroundState_3dGameplay_UseTheForce implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("3dGameplay.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_Outcome_YouWin();
        quitButton.setVisible(false);
        quitButton.setEnabled(true);
        startButton.setVisible(false);
        helpButton.setVisible(false);
        repairButton.setVisible(false);
        backUpButton.setVisible(false);
        useTheForceButton.setVisible(false);
    }
}
