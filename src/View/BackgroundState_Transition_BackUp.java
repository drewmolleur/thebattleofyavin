package View;

import java.awt.*;

public class BackgroundState_Transition_BackUp implements BackgroundState {

    AnimatedImage image = AnimatedImage.load(getClass().getResource("backUp.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_3dGameplay_UseTheForce();
    }
}
