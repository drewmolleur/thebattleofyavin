package Controller.Observer2;

import java.util.ArrayList;

public interface Subject2 {
    ArrayList<Observer2> observables = new ArrayList();

    void NotifyObservers(String var1);
    void RegisterObserver(Observer2 var1);
    void DeregisterObserver(Observer2 var1);

}
