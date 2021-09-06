package Controller.Observer;

import java.util.EventObject;

public class tieFighterCreateEvent extends EventObject {

    private int x;
    private int y;

    public tieFighterCreateEvent(Object source, int x, int y) {
        super(source);
        this.x = x;
        this.y = y;
    }

    public int getX() { return x;}
    public int getY() { return y;}
}
