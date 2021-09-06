package Model.Missile;

import Controller.Main;
import Model.GameFigure;
import Model.Shooter;

import java.awt.*;
import java.awt.geom.Point2D;

public class Missile extends GameFigure {

    public static final int UNIT_MOVE = 20;
    public static final int INIT_MISSILE_SIZE = 10;
    public static final int MAX_MISSILE_SIZE = 15;

    public static final int STATE_SHOOTING = 0;
    public static final int STATE_EXPLODING = 1;
    public static final int STATE_DONE = 2;

    int size = INIT_MISSILE_SIZE;
    Point2D.Float target;
    Color color1;
    Color color2;
    int state;

    MissileAnimStrategy animStrategy;

    public Missile(int tx, int ty) {
        Shooter shooter = (Shooter) Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER);
        super.location.x = shooter.barrelL.x2-250;   // Move Missile to Center of xWing
        super.location.y = shooter.barrelL.y2;
        super.location.x = shooter.barrelR.x2-50; // Move Missile to Center of xWing
        super.location.y = shooter.barrelR.y2;
        target = new Point2D.Float(tx, ty);
        color1 = Color.RED;
        color2 = Color.BLUE;
        state = STATE_SHOOTING;
        animStrategy = new MissileAnimShooting(this);
    }

    @Override
    public void render(Graphics2D g2) {
        g2.setColor(color1);
        g2.setStroke(new BasicStroke(1));
        g2.fillOval((int) super.location.x - size / 2, (int) super.location.y - size / 2, size, size);
    }

    @Override
    public void update() {
        updateState();
        animStrategy.animate();
    }

    private void updateState() {
        if (state == STATE_SHOOTING) {
            if (hitCount > 0 || target.distance(location) <= 3.0) {
                state = STATE_EXPLODING;
                animStrategy = new MissileAnimExploding(this);
            }
        } else if (state == STATE_EXPLODING) {
            if (size >= MAX_MISSILE_SIZE) {
                state = STATE_DONE;
            }
        } else if (state == STATE_DONE) {
            super.done = true;
        }
    }

    @Override
    public int getCollisionRadius() {
        return size / 2;
    }
}
