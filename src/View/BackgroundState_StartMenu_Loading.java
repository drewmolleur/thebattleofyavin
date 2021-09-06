package View;

import Controller.Main;

import javax.swing.*;
import java.awt.*;

public class BackgroundState_StartMenu_Loading implements BackgroundState {

    ImageIcon icon = new ImageIcon(this.getClass().getResource("loading.gif"));
    Image image = icon.getImage();

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(image,0,0,null);
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
