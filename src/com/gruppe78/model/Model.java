package com.gruppe78.model;

import java.util.ArrayList;

/**
 * Because Model is synchronized there should be as little interaction with it as possible.
 * Maybe replace the Singleton pattern with static methods since there may be little use of polymorphism capabilities here.
 */
public class Model {
    private static Model sModel = new Model();

    private ArrayList<ModelChangedListener> listenerList = new ArrayList<>();
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private boolean[][] mButtonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];
    private Floor mFloor;

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Eager initialization.
     ****************************************************************************************************************/

    private Model(){}

    public static Model get(){
        return sModel;
    }


    /****************************************************************************************************************
     * Adding and removing of listeners.
     ****************************************************************************************************************/

    public synchronized void addModelChangedListener(ModelChangedListener listener){
        listenerList.add(listener);
    }
    public synchronized void removeModelChangedListener(ModelChangedListener listener){
        listenerList.remove(listener);
    }


    /***************************************************************************************************************
     * The list of elevators
     ***************************************************************************************************************/

    public synchronized void addElevator(Elevator elevator){
        elevators.add(elevator);
    }

    public synchronized void removeElevator(Elevator elevator){
        elevators.remove(elevator);
    }

    /***************************************************************************************************************
     * Elevator specific implementations.
     ***************************************************************************************************************/

    public synchronized void setButtonPressed(Floor floor, Button button, boolean pressed){
        if(mButtonPressed[floor.index][button.index] == pressed) return;
        mButtonPressed[floor.index][button.index] = pressed;

        for(ModelChangedListener listener : listenerList){
            listener.onButtonPressed(floor, button, pressed);
        }
    }

    public synchronized void setFloor(Floor floor){
        if(floor == mFloor) return;
        mFloor = floor;

        for(ModelChangedListener listener : listenerList){
            listener.onFloorChanged(floor);
        }
    }
}
