package View;

import Model.GameData;

import java.awt.*;

import static View.MyWindow.repairButton;

public class BackgroundState_2dGameplay_Repair implements BackgroundState {

    AnimatedImage image = AnimatedImage.load(getClass().getResource("repair.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        GameData.loaded = true;
        repairButton.setEnabled(false);
        canvas.backgroundState = new BackgroundState_2dGameplay_Offense();
    }
}
