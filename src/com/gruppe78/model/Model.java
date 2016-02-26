package com.gruppe78.model;

import com.gruppe78.ModelChangedListener;

import java.util.ArrayList;

/**
 * Created by student on 12.02.16.
 */
public class Model {
    private static Model sModel;
    ArrayList<ModelChangedListener> listenerList = new ArrayList<>();
    ArrayList<Elevator> elevators = new ArrayList<>();

    private Model(){

    }

    public static Model get(){
        if(sModel == null){
            sModel = new Model();
        }
        return sModel;
    }

    public synchronized void addModelChangedListener(ModelChangedListener listener){
        listenerList.add(listener);
    }

    public synchronized void setElevatorFloor(int i){
        //Add change.
        for(ModelChangedListener listener : listenerList){
            listener.onFloorChanged();
        }
    }
}
