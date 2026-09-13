package Controller;

import Controller.Observer.tieFighterCreateEvent;
import Model.Missile.Missile;
import Model.MousePointer;
import Model.Shooter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class PlayerInputEventQueue {

    public LinkedList<InputEvent> queue = new LinkedList<>();

    public void processInputEvents() {

        while (!queue.isEmpty()) {

            InputEvent inputEvent = queue.removeFirst();

            switch (inputEvent.type) {

                case InputEvent.MOUSE_PRESSED:
                    MouseEvent e = (MouseEvent) inputEvent.event;
                    Point target = Main.win.canvas.toGamePoint(e.getX(), e.getY());
                    Missile m = new Missile(target.x, target.y);
                    Missile m2 = new Missile(target.x + 100, target.y);
                    Main.gameData.friendObjects.add(m);
                    Main.gameData.friendObjects.add(m2);
                    Main.xWingMissile.start();
                    break;
                case InputEvent.MOUSE_MOVED:
                    MousePointer mp = (MousePointer) Main.gameData.fixedObjects.get(0);
                    MouseEvent me = (MouseEvent) inputEvent.event;
                    Point pointer = Main.win.canvas.toGamePoint(me.getX(), me.getY());
                    mp.location.x = pointer.x;
                    mp.location.y = pointer.y;
                    break;
                case InputEvent.KEY_PRESSED:
                    var shooter = Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER);
                    KeyEvent ke = (KeyEvent) inputEvent.event;
                    switch (ke.getKeyCode()) {
                        case KeyEvent.VK_W:
                            shooter.location.y -= Shooter.UNIT_MOVE;
                            break;
                        case KeyEvent.VK_S:
                            shooter.location.y += Shooter.UNIT_MOVE;
                            break;
                        case KeyEvent.VK_A:
                            shooter.location.x -= Shooter.UNIT_MOVE;
                            break;
                        case KeyEvent.VK_D:
                            shooter.location.x += Shooter.UNIT_MOVE;
                            break;
                    }
                    break;
                case InputEvent.tieFighter_Create:
                    tieFighterCreateEvent ue = (tieFighterCreateEvent) inputEvent.event;
                    Main.addTieFighterWithListener(ue.getX(),ue.getY());
            }
        }
    }
}