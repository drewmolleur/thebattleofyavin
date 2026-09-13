package View;

import java.awt.*;

public class BackgroundState_Outcome_YouWin implements BackgroundState {

    AnimatedImage image = new AnimatedImage(getClass().getResource("youWin.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_StartMenu_Dashboard();
    }
}
