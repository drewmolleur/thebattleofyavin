package Model;

import Controller.Observer2.Observer2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static Controller.Observer2.Subject2.observables;

public abstract class GameFigure {

    public BufferedImage image;
    public Point2D.Float location;
    public boolean done = false;
    public int hitCount = 0;
    public int points;
    public int x;
    public int y;

    public GameFigure(float x, float y) {
        location = new Point2D.Float(x, y);
    }

    public GameFigure() {
        this(0, 0);
    }

    public void setLocation(float x, float y) {
        location.x = x;
        location.y = y;
    }

    public boolean collideWith(GameFigure o) {
        double dist = this.location.distance(o.location);
        if (dist <= this.getCollisionRadius() + o.getCollisionRadius()) {
            return true;
        } else
            return false;
    }

    public abstract void render(Graphics2D g2);
    public abstract void update();
    public abstract int getCollisionRadius();

    public BufferedImage LoadImage(String filename){
        BufferedImage img = null;

        try {
            img = ImageIO.read(this.getClass().getResource("/View/"+filename));
        } catch (Exception var4) {
            System.out.println(var4.getMessage());
        }

        if (img != null) {
            this.image = img;
        }   else {
            System.out.println(filename);
        }
        return img;
    }

    public void NotifyObservers(String event) {
        for(int i = 0; i < observables.size(); ++i) {
            (observables.get(i)).getNotification(event);
        }
    }

    public static void RegisterObserver(Observer2 observer) { observables.add(observer);}
    public void DeregisterObserver(Observer2 observer) { observables.remove(observables.indexOf(observer));}
    public BufferedImage getImage() {return this.image;}
}