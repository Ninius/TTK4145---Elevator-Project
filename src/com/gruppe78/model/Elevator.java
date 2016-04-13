package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;

public class Elevator {
    private final InetAddress mInetAddress; //Also serves as ID.

    //Orders:
    private final Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];

    //Position and direction:
    private Floor mFloor;
    private Floor mLastKnownFloor;
    private Direction mOrderDirection;
    private Direction mMotorDirection;
    private volatile boolean mDoorOpen;

    //Status:
    private volatile boolean mConnected = false;
    private volatile boolean mOperable = true;
    private volatile long lastConnect = 0;

    //Listeners:
    private final ArrayList<ElevatorPositionListener> positionListeners = new ArrayList<>();
    private final ArrayList<OrderListener> orderListeners = new ArrayList<>();
    private final ArrayList<ElevatorStatusListener> statusListeners = new ArrayList<>();

    public Elevator(InetAddress InetAddress){
        mInetAddress = InetAddress;
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

    public InetAddress getInetAddress(){
        return mInetAddress;
    }

    public void setConnected(boolean connected){
        if(connected) lastConnect = System.currentTimeMillis();

        if(connected == mConnected) return;
        mConnected = connected;
        for(ElevatorStatusListener listener : statusListeners){
            listener.onConnectionChanged(mConnected);
        }
    }
    public boolean isConnected(){
        return mConnected;
    }
    public long getLastConnectTime(){
        return lastConnect;
    } //Possibly not needed.

    public boolean isOperable() {return mOperable;}
    public void setOperable(boolean mOperable) {
        this.mOperable = mOperable;
    }

    /************************************************************
     * Identifiers:
     ************************************************************/

    public boolean isLocal(){
        return this == SystemData.get().getLocalElevator();
    }
    public boolean hasHigherIDThan(Elevator elevator){
        byte[] thisAddress = this.getInetAddress().getAddress();
        byte[] otherAddress = elevator.getInetAddress().getAddress();
        for(int i = 0; i < thisAddress.length || i < otherAddress.length; i++){
            if(thisAddress[i] > otherAddress[i]) return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return "E("+getInetAddress().getHostAddress()+")";
    }
}
