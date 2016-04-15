package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;

public class Elevator {
    private final InetAddress mInetAddress; //Also serves as ID.
    private final int mID;

    //Orders:
    private final Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];

    //Position and direction:
    private Floor mFloor;
    private Floor mLastKnownFloor;
    private Direction mOrderDirection;
    private Direction mMotorDirection;
    private boolean mDoorOpen;

    //Status:
    private volatile boolean mConnected = false;
    private volatile boolean mOperable = true;
    private volatile long lastConnect = 0;

    //Listeners:
    private final ArrayList<ElevatorPositionListener> positionListeners = new ArrayList<>();
    private final ArrayList<OrderListener> orderListeners = new ArrayList<>();
    private final ArrayList<ElevatorStatusListener> statusListeners = new ArrayList<>();

    public Elevator(InetAddress inetAddress){
        this(inetAddress,-1);
    }
    public Elevator(InetAddress inetAddress, int ID){
        mInetAddress = inetAddress;
        mID = ID;
    }

    /****************************************************************************************************************
     * Adding and removing of listeners.
     ****************************************************************************************************************/

    public synchronized void addElevatorMovementListener(ElevatorPositionListener listener){
        positionListeners.add(listener);
    }
    public synchronized void removeElevatorMovementListener(ElevatorPositionListener listener){
        positionListeners.remove(listener);
    }
    public synchronized void addElevatorStatusListener(ElevatorStatusListener listener){
        statusListeners.add(listener);
    }
    public synchronized void removeElevatorStatusListener(ElevatorStatusListener listener){
        statusListeners.remove(listener);
    }

    public void addOrderEventListener(OrderListener listener){
        synchronized (orderListeners){
            orderListeners.add(listener);
        }
    }
    public void removeOrderEventListener(OrderListener listener){
        synchronized (orderListeners) {
            orderListeners.remove(listener);
        }
    }

    /**************************************************************************************************************
     * Movement
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

        for(ElevatorPositionListener listener : positionListeners){
            listener.onFloorChanged(newFloor);
        }
    }

    public synchronized void setOrderDirection(Direction direction){
        if(mOrderDirection == direction) return;
        mOrderDirection = direction;
        for(ElevatorPositionListener listener : positionListeners){
            listener.onOrderDirectionChanged(direction);
        }
    }
    public synchronized Direction getOrderDirection(){
        return mOrderDirection;
    }

    public synchronized void setMotorDirection(Direction direction) {
        if(direction == mMotorDirection) return;
        mMotorDirection =  direction;
        for(ElevatorPositionListener listener : positionListeners){
            listener.onMotorDirectionChanged(direction);
        }
    }
    public synchronized Direction getMotorDirection(Direction direction) { return mMotorDirection;}

    public synchronized void setDoor(boolean open){
        if(mDoorOpen == open) return;
        mDoorOpen = open;
        for(ElevatorPositionListener listener : positionListeners){
            listener.onDoorOpenChanged(open);
        }
    }
    public synchronized boolean isDoorOpen(){
        return mDoorOpen;
    }

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

    public InetAddress getAddress(){
        return mInetAddress;
    }

    public synchronized void setConnected(boolean connected){
        if(connected) lastConnect = System.currentTimeMillis();

        if(connected == mConnected) return;
        mConnected = connected;
        for(ElevatorStatusListener listener : statusListeners){
            listener.onConnectionChanged(this, mConnected);
        }
    }
    public synchronized boolean isConnected(){
        return mConnected;
    }
    public long getLastConnectTime(){
        return lastConnect;
    } //Possibly not needed.

    public synchronized boolean isOperable() {
        return mOperable;
    }
    public synchronized void setOperable(boolean operable) {
        if (mOperable == operable) return;
        mOperable = operable;
        for (ElevatorStatusListener listener : statusListeners){
            listener.onOperableChanged(this, mOperable);
        }
    }

    /************************************************************
     * Identifiers:
     ************************************************************/

    public boolean isLocal(){
        return this == SystemData.get().getLocalElevator();
    }
    public int getID(){ return mID;}

    @Override
    public String toString(){
        return "E"+mID+"("+ getAddress().getHostAddress()+")";
    }
}
