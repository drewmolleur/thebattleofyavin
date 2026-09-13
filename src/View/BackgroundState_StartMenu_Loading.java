package View;

import Controller.Main;

import java.awt.*;

public class BackgroundState_StartMenu_Loading implements BackgroundState {

    AnimatedImage image = AnimatedImage.load(getClass().getResource("loading.gif"));

    @Override
    public void render(Graphics2D g2) {
        image.draw(g2, 0, 0);
        for (var fig : Main.gameData.fixedObjects) {
            fig.render(g2);
        }
        for (var fig : Main.gameData.friendObjects) {
            fig.render(g2);
        }
        for (var fig : Main.gameData.enemyObjects) {
            fig.render(g2);
        }
    }

    @Override
    public void goNext(MyCanvas canvas) {
        canvas.backgroundState = new BackgroundState_Transition_Offense();
    }
}
