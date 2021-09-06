package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseEventListener extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
        //Main.xWingMissile.start();
        InputEvent inputEvent = new InputEvent();
        inputEvent.event = e;
        inputEvent.type = InputEvent.MOUSE_PRESSED;
        Main.playerInputEventQueue.queue.add(inputEvent);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Main.xWingMissile.stop();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("Mouse moved at " + e.getX() + " " + e.getY());
        InputEvent inputEvent = new InputEvent();
        inputEvent.event = e;
        inputEvent.type = InputEvent.MOUSE_MOVED;
        Main.playerInputEventQueue.queue.add(inputEvent);
    }
}