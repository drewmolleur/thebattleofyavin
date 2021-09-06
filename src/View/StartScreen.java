package View;

import Model.GameFigure;

import java.awt.*;

public class StartScreen extends GameFigure
{

    public StartScreen(int x, int y) {
        super(x, y);
        this.LoadImage("3dGameplay.jpg");
    }

    public void render(Graphics2D g2) { g2.drawImage(this.image, 0, 0, null); }

    public void update() {}

    public int getCollisionRadius() { return 0; }

    public int getSize() { return 0; }
}