package com.gruppe78.model;

import java.util.ArrayList;
import java.util.function.ObjDoubleConsumer;

/**
 * Created by student on 26.02.16.
 */
public class Elevator {
    private Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];
    private ArrayList<Order> orderList = new ArrayList<>();
    private boolean[][] mButtonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];
    private Floor mFloor;
    private Floor mLastFloor;
    private Object floorLockObject = new Object();
    private boolean mLocal;

    private ArrayList<ElevatorEventListener> listenerList = new ArrayList<>();

    public Elevator(boolean local){
        mLocal = local;
    }


    /****************************************************************************************************************
     * Adding and removing of listeners.
     ****************************************************************************************************************/

    public synchronized void addElevatorEventListener(ElevatorEventListener listener){
        listenerList.add(listener);
    }
    public synchronized void removeElevatorEventListener(ElevatorEventListener listener){
        listenerList.remove(listener);
    }

    /***************************************************************************************************************
     * Elevator specific implementations.
     ***************************************************************************************************************/

    public synchronized void setButtonPressed(Floor floor, Button button, boolean pressed){
        if(mButtonPressed[floor.index][button.index] == pressed) return;
        mButtonPressed[floor.index][button.index] = pressed;

        for(ElevatorEventListener listener : listenerList){
            listener.onButtonPressed(floor, button, pressed);
        }
    }
    public Floor getFloor(){
        return mFloor;
    }
    public int getDirection(){
        return mFloor.index - mLastFloor.index;
    }
    public Order getNextOrder(){
        return orderList.get(0);
    }
    //Replace with floor type?
    public void clearOrder(Floor floor){
        for (Order order: orderList){
            if (order.getFloor().index == floor.index){
                orderList.remove(order);
                //Network call to clear other elevators?
            }
        }
    }
    public void setFloor(Floor floor){
        synchronized (floorLockObject){
            if(floor == mFloor) return;
            if(floor == null){
                mLastFloor = mFloor;
            }else{
                mLastFloor = floor;
            }
            mFloor = floor;

            for(ElevatorEventListener listener : listenerList){
                listener.onFloorChanged(floor);
            }
        }
    }

    public boolean isLocal(){
        return mLocal;
    }


}
