package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;

public class Elevator {
    private final InetAddress mInetAddress;

    private final Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];

    //Position and direction:
    private Floor mFloor;
    private Floor mLastKnownFloor;
    private Direction mOrderDirection;
    private Direction mMotorDirection;

    //Status:
    private volatile boolean mConnected = false;
    private volatile boolean mOperable = true;
    private volatile long lastConnect = 0;

    //Listeners:
    private final ArrayList<ElevatorPositionListener> listenerList = new ArrayList<>();
    private final ArrayList<OrderEventListener> orderEventListeners = new ArrayList<>();
    private final ArrayList<ElevatorNetworkListener> networkListeners = new ArrayList<>();

    public Elevator(InetAddress InetAddress){
        mInetAddress = InetAddress;
    }

    /****************************************************************************************************************
     * Adding and removing of listeners.
     ****************************************************************************************************************/

    public synchronized void addElevatorEventListener(ElevatorPositionListener listener){
        listenerList.add(listener);
    }
    public synchronized void removeElevatorEventListener(ElevatorPositionListener listener){
        listenerList.remove(listener);
    }
    public synchronized void addElevatorNetworkListener(ElevatorNetworkListener listener){
        networkListeners.add(listener);
    }
    public synchronized void removeElevatorNetworkListener(ElevatorNetworkListener listener){
        networkListeners.remove(listener);
    }

    public void addOrderEventListener(OrderEventListener listener){
        synchronized (orderEventListeners){
            orderEventListeners.add(listener);
        }
    }
    public void removeOrderEventListener(OrderEventListener listener){
        synchronized (orderEventListeners) {
            orderEventListeners.remove(listener);
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

        for(ElevatorPositionListener listener : listenerList){
            listener.onFloorChanged(newFloor);
        }
    }
    public synchronized void setOrderDirection(Direction direction){
        if(mOrderDirection == direction) return;
        mOrderDirection = direction;
    }
    public synchronized Direction getOrderDirection(){
        return mOrderDirection;
    }

    public synchronized void setMotorDirection(Direction direction) {
        if(direction == mMotorDirection) return;
        mMotorDirection =  direction;
        for(ElevatorPositionListener listener : listenerList){
            listener.onMotorDirectionChanged(direction);
        }
    }
    public synchronized Direction getMotorDirection(Direction direction) { return mMotorDirection;}

    /**************************************************************************************************************
     * Internal orders
     **************************************************************************************************************/

    public Order getInternalOrder(Floor floor){
        return internalOrders[floor.index];
    }
    public void addInternalOrder(Floor floor, Button button){
        internalOrders[floor.index] = new Order(this, button, floor);
    }
    public void clearInternalOrder(Floor floor){
        internalOrders[floor.index] = null;
    }

    /*************************************************
     * Status - Connection and Operability
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

    public boolean isOperable() {return mOperable;}
    public void setOperable(boolean mOperable) {
        this.mOperable = mOperable;
    }
    public boolean isLocal(){
        return this == SystemData.get().getLocalElevator();
    }
}
