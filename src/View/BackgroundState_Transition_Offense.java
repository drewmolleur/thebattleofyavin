package View;

import Model.GameData;

import java.awt.*;

public class BackgroundState_Transition_Offense implements BackgroundState {

    AnimatedImage image = new AnimatedImage(getClass().getResource("begin.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        GameData.loaded = true;
        canvas.backgroundState = new BackgroundState_2dGameplay_Offense();
    }
}
