package Model;

import Controller.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Shooter extends GameFigure {

    private final int HEIGHT = 100;
    private final int WIDTH = 100;

    public final int BASE_SIZE = 200;
    public final int BARREL_LEN = 20;

    public static final int UNIT_MOVE = 40;

    public Rectangle2D.Float base;
    public Line2D.Float barrelL;
    public Line2D.Float barrelR;
    private Image image;

    public Shooter(int x, int y) {
        super(x, y);
        base = new Rectangle2D.Float(x - BASE_SIZE/2, y - BASE_SIZE/2, BASE_SIZE, BASE_SIZE);
        barrelL = new Line2D.Float(x, y, x, y - BARREL_LEN);
        barrelR = new Line2D.Float(x, y, x, y - BARREL_LEN);
        try {
            this.image = ImageIO.read(this.getClass().getResource("xWing.png"));
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: Cannot open shooter.png");
            System.exit(-1);
        }
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(this.image, (int)base.x, (int)base.y, WIDTH, HEIGHT, null);
    }

    @Override
    public void update() {
        MousePointer mousePointer = (MousePointer) Main.gameData.fixedObjects.get(Main.INDEX_MOUSE_POINTER);
        float tx = mousePointer.location.x;
        float ty = mousePointer.location.y;
        double rad = Math.atan2(ty - super.location.y, tx - super.location.x);
        float barrel_y = (float) (BARREL_LEN * Math.sin(rad));
        float barrel_x = (float) (BARREL_LEN * Math.cos(rad));
        barrelL.x1 = super.location.x;
        barrelL.y1 = super.location.y;
        barrelL.x2 = super.location.x + barrel_x;
        barrelL.y2 = super.location.y + barrel_y;
        barrelR.x1 = super.location.x + 20;
        barrelR.y1 = super.location.y;
        barrelR.x2 = super.location.x + 20 + barrel_x;
        barrelR.y2 = super.location.y + barrel_y;
        base.x = location.x - BASE_SIZE / 2;
        base.y = location.y - BASE_SIZE / 2;
    }

    @Override
    public int getCollisionRadius() {
        return 10;
    }
}