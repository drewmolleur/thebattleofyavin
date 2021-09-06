package Controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyEventListener extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            //case KeyEvent.VK_W: System.out.println("Up Key"); break;
            //case KeyEvent.VK_S: System.out.println("Down Key"); break;
            //case KeyEvent.VK_A: System.out.println("Left Key"); break;
            //case KeyEvent.VK_D: System.out.println("Right Key"); break;
        }
        InputEvent inputEvent = new InputEvent();
        inputEvent.event = e;
        inputEvent.type = InputEvent.KEY_PRESSED;
        Main.playerInputEventQueue.queue.add(inputEvent);
    }
}