package Model.tieFighterBlaster;

import Controller.Main;
import Model.GameFigure;
import Model.Vader.Vader;
import Model.tieFighter.tieFighter;

import java.awt.*;
import java.awt.geom.Point2D;

public class tieFighterBlaster extends GameFigure {

    public static final int UNIT_MOVE = 20;
    public static final int INIT_MISSILE_SIZE = 10;
    public static final int MAX_MISSILE_SIZE = 30;

    public static final int STATE_SHOOTING = 0;
    public static final int STATE_EXPLODING = 1;
    public static final int STATE_DONE = 2;

    public static tieFighter tieFighter;

    int size = INIT_MISSILE_SIZE;
    Point2D.Float target; // where mouse was pressed
    Color color;
    int state;

    tieFighterBlasterAnimStrategy animStrategy;

    public tieFighterBlaster(int tx, int ty, tieFighter tieFighter) {
        this.tieFighter = tieFighter;
        super.location.x = tieFighter.location.x+50;
        super.location.y = tieFighter.location.y+50;
        target = new Point2D.Float(Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER).location.x, Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER).location.y);
        color = Color.RED;
        state = STATE_SHOOTING;
        animStrategy = new tieFighterBlasterAnimShooting(this);
    }

    @Override
    public void render(Graphics2D g2) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1));
        g2.fillOval((int) super.location.x - size / 2, (int) super.location.y - size / 2, size, size);
    }

    @Override
    public void update() {
        updateState();
        if(state == STATE_SHOOTING) {
            // travel along the path to the target
            double rad = Math.atan2(target.y - location.y, target.x - location.x);
            location.x += UNIT_MOVE * Math.cos(rad);
            location.y += UNIT_MOVE * Math.sin(rad);
        } else if(state == STATE_EXPLODING) {
            // explosion effect
            color = Color.ORANGE;
            ++size;
        }
    }

    private void updateState() {
        if (state == STATE_SHOOTING) {
            if (hitCount > 0 || target.distance(location) <= 3.0) {
                state = STATE_EXPLODING;
                animStrategy = new tieFighterBlasterAnimExploding(this);
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
