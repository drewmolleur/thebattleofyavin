package Controller.Observer;

import Controller.InputEvent;
import Controller.Main;

public class tieFighterObserverAddNew implements Observer {

    @Override
    public void eventRecieved()
    {
        System.out.println("tieFighter died");
        Main.addTieFighterWithListener(100,200);
        InputEvent event = new InputEvent();
        event.event = new tieFighterCreateEvent("tieFighter",100,100);
        event.type = InputEvent.tieFighter_Create;
        Main.playerInputEventQueue.queue.add(event);
    }
}
