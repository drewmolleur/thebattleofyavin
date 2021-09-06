package Model.Vader;

import Controller.Main;
import Controller.Observer.Observer;
import Controller.Observer.Subject;
import Model.GameFigure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Vader extends GameFigure implements Subject {

    public static int UNIT_MOVE = 20;
    public static int UNIT_MOVE_FALLING = 150;

    public static final int STATE_FLYING = 0;
    public static final int STATE_FALLING = 1;
    public static final int STATE_DONE = 2;

    int size = 100;
    int width, height;
    boolean movingRight = true;

    int state;
    Color color;
    VaderAnimStrategy animStrategy;

    private final int HEIGHT = 100;
    private final int WIDTH = 100;

    private Image image;

    ArrayList<Observer> listeners = new ArrayList<>();

    public Vader(final float x, final float y) {
        super(x, y);
        width = size;
        height = size;
        state = STATE_FLYING;
        try {
            this.image = ImageIO.read(this.getClass().getResource("vader.png"));
            animStrategy = new VaderAnimFlying(this);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: Cannot open shooter.png");
            System.exit(-1);
        }
    }

    @Override
    public void render(Graphics2D g2) {
        g2.drawImage(this.image, (int)location.x-50, (int)location.y-50, WIDTH, HEIGHT, null);
        //g2.setColor(color);
        //g2.setStroke(new BasicStroke(10));
        //g2.fillOval((int) location.x - width / 2, (int) location.y - height / 2, width, height);
    }

    @Override
    public void update() {
        updateState();
        animStrategy.animate(); // strategy design pattern
    }

    private void updateState() {
        if (state == STATE_FLYING) {
            if (hitCount > 0) {
                state = STATE_FALLING;
                animStrategy = new VaderAnimFalling(this);
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