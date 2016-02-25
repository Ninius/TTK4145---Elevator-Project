package com.gruppe78.model;

import com.gruppe78.StateChanged;

import java.util.ArrayList;

/**
 * Created by student on 12.02.16.
 */
public class Model {
    ArrayList<StateChanged> listenerList = new ArrayList<>();
    int elevatorfloor;
    public void setListener(StateChanged listener){
        listenerList.add(listener);
    }
    public synchronized void setElevatorfloor(int newElevatorFLoor){
        elevatorfloor = newElevatorFLoor;
        for(StateChanged listener : listenerList){
            listener.onFloorChanged();
        }
    }
}
