package View;

import javax.swing.*;
import java.awt.*;

public class BackgroundState_Transition_BackUp implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("backUp.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_3dGameplay_UseTheForce();
    }
}
