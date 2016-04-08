package com.gruppe78.model;

import com.gruppe78.utilities.Utilities;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.function.ObjDoubleConsumer;

/**
 * Created by student on 26.02.16.
 */
public class Elevator {
    private final String mIPAddress; //Serves as the unique identifier

    //Orders:
    private Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];

    private boolean[][] mButtonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];

    //Position and direction:
    private Floor mFloor;
    private Floor mLastFloor;

    //Network:
    private DatagramSocket UDPSocket;

    private ArrayList<ElevatorEventListener> listenerList = new ArrayList<>();

    public Elevator(String IPAddress){
        mIPAddress = IPAddress;
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
        if (button == Button.INTERNAL && pressed){
            addInternalOrder(floor, button);
        }
        else{
            Model.get().addGlobalOrder(new Order(this, button, floor));
        }
        for(ElevatorEventListener listener : listenerList){
            listener.onButtonPressed(floor, button, pressed);
        }
    }
    public Floor getFloor(){
        return mFloor;
    }
    public Order getInternalOrder(int floor){
        return internalOrders[floor];
    }
    public int getNumberOfInternalOrders(){
        int orders = 0;
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            if (internalOrders[i] != null){
                orders++;
            }
        }
        return orders;
    }

    public int getDirection(){
        return mFloor.index - mLastFloor.index;
    }

    //Replace with floor type?
    public void addInternalOrder(Floor floor, Button button){
        internalOrders[floor.index] = new Order(this, button, floor);
    }
    public void clearInternalOrder(Floor floor){
        internalOrders[floor.index] = null;
    }
    public synchronized void setFloor(Floor floor){
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

    public String getIPAddress(){
        return mIPAddress;
    }

    public boolean isLocal(){
        return Utilities.getLocalAddress() != null && Utilities.getLocalAddress() == mIPAddress;
    }
}
