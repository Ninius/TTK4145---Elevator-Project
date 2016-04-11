package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;

public class Elevator {
    private final InetAddress mInetAddress;
    private final boolean mLocal;

    private Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];
    private boolean[][] mButtonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];

    //Position and direction:
    private Floor mFloor;
    private Floor mLastKnownFloor;
    private Direction mDirection;

    //Status:
    private volatile boolean mConnected = false;
    private volatile boolean mOperable = true;
    private volatile long lastConnect = 0;

    //Listeners:
    private ArrayList<ElevatorEventListener> listenerList = new ArrayList<>();
    private ArrayList<ElevatorNetworkListener> networkListeners = new ArrayList<>();

    public Elevator(InetAddress InetAddress, boolean local){
        mInetAddress = InetAddress;
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
    public synchronized void addElevatorNetworkListener(ElevatorNetworkListener listener){
        networkListeners.add(listener);
    }
    public synchronized void removeElevatorNetworkListener(ElevatorNetworkListener listener){
        networkListeners.remove(listener);
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
            SystemData.get().addGlobalOrder(new Order(this, button, floor));
        }
        for(ElevatorEventListener listener : listenerList){
            listener.onButtonPressed(floor, button, pressed);
        }
    }

    /**************************************************************************************************************
     * Position and movement
     **************************************************************************************************************/
    public synchronized Floor getLastKnownFloor(){
        return mLastKnownFloor;
    }
    public synchronized Floor getFloor(){
        return mFloor;
    }
    public synchronized void setFloor(Floor newFloor){
        if(newFloor == mFloor) return;
        if(newFloor == null){
            mLastKnownFloor = mFloor;
        }else{
            mLastKnownFloor = newFloor;
        }
        mFloor = newFloor;

        for(ElevatorEventListener listener : listenerList){
            listener.onFloorChanged(newFloor);
        }
    }

    /**************************************************************************************************************
     * Internal orders
     **************************************************************************************************************/

    public Order getInternalOrder(Floor floor){
        return internalOrders[floor.index];
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

    public synchronized void setDirection(Direction direction){
        mDirection = direction;
    }
    public synchronized Direction getDirection(){
        return mDirection;
    }

    //Replace with floor type?
    public void addInternalOrder(Floor floor, Button button){
        internalOrders[floor.index] = new Order(this, button, floor);
    }
    public void clearInternalOrder(Floor floor){
        internalOrders[floor.index] = null;
    }

    public boolean isLocal(){
        return mLocal;
    }

    /*************************************************
     * Connection
     *************************************************/

    public InetAddress getInetAddress(){
        return mInetAddress;
    }

    public void setConnected(boolean connected){
        if(connected) lastConnect = System.currentTimeMillis();

        if(connected == mConnected) return;
        mConnected = connected;
        for(ElevatorNetworkListener listener : networkListeners){
            listener.onConnectionChanged(mConnected);
        }
    }
    public boolean isConnected(){
        return mConnected;
    }
    public long getLastConnectTime(){
        return lastConnect;
    }
}
