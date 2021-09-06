package Model.tieFighter;

import Controller.Main;
import Controller.Observer.Observer;
import Controller.Observer.Subject;
import Model.GameFigure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class tieFighter extends GameFigure implements Subject {

    public static int UNIT_MOVE = 10;
    public static int UNIT_MOVE_FALLING = 20;

    public static final int STATE_FLYING = 0;
    public static final int STATE_FALLING = 1;
    public static final int STATE_DONE = 2;

    int size = 100;
    int width, height;
    boolean movingRight = true;

    int state;
    Color color;
    tieFighterAnimStrategy animStrategy;

    private final int HEIGHT = 100;
    private final int WIDTH = 100;

    private Image image;

    ArrayList<Observer> listeners = new ArrayList<>();

    public tieFighter(final float x, final float y) {
        super(x, y);
        width = size;
        height = size;
        state = STATE_FLYING;
        animStrategy = new tieFighterAnimFlying(this);
        try {
            this.image = ImageIO.read(this.getClass().getResource("tieFighter.png"));
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: Cannot open shooter.png");
            System.exit(-1);
        }
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(this.image, (int)location.x-50, (int)location.y-50, WIDTH, HEIGHT, null);
    }

    @Override
    public void update() {
        updateState();
        animStrategy.animate(); // strategy design pattern
    }

    private void updateState() {
        if (state == STATE_FLYING) {
            if (hitCount > 0) {
                try {
                    this.image = ImageIO.read(this.getClass().getResource("tieFighterDone.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                state = STATE_FALLING;
                animStrategy = new tieFighterAnimFalling(this);
            }
        } else if (state == STATE_FALLING) {
            if (location.y >= Main.win.canvas.height) {
                state = STATE_DONE;
            }
        } else if (state == STATE_DONE) {
            super.done = true;
            notifyEvent(); // UFO died
        }
    }

    @Override
    public int getCollisionRadius() {
        return (int) (size / 2 * 0.75);
    }

    @Override
    public void attachListener(Observer o) {
        listeners.add(o);
    }

    @Override
    public void detachListener(Observer o) {
        listeners.remove(o);
    }

    @Override
    public void notifyEvent() {
        for (var o: listeners) {
            o.eventRecieved();
        }
    }
}