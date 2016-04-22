package com.gruppe78.model;

import com.gruppe78.utilities.Log;

import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Elevator {
    private final InetAddress mInetAddress; //Also serves as ID.
    private final int mID;

    //Orders:
    private final Order[] internalOrders = new Order[Floor.NUMBER_OF_FLOORS];

    //Position and direction:
    private Floor mFloor;
    private Direction mOrderDirection = Direction.UP;
    private Direction mMotorDirection;
    private AtomicBoolean mDoorOpen = new AtomicBoolean(false);

    //Status:
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private AtomicBoolean mOperable =  new AtomicBoolean(true);

    //Listeners:
    private final CopyOnWriteArrayList<ElevatorPositionListener> positionListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<OrderListener> orderListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ElevatorStatusListener> statusListeners = new CopyOnWriteArrayList<>();

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

    public void addElevatorPositionListener(ElevatorPositionListener listener){
        positionListeners.add(listener);
    }
    public void removeElevatorMovementListener(ElevatorPositionListener listener){
        positionListeners.remove(listener);
    }

    public void addElevatorStatusListener(ElevatorStatusListener listener){
        statusListeners.add(listener);
    }
    public void removeElevatorStatusListener(ElevatorStatusListener listener){
        statusListeners.remove(listener);
    }

    public void addOrderEventListener(OrderListener listener){
        orderListeners.add(listener);
    }
    public void removeOrderEventListener(OrderListener listener){
        orderListeners.remove(listener);
    }

    /**************************************************************************************************************
     * Movement
     **************************************************************************************************************/

    public synchronized Floor getFloor(){
        return mFloor;
    }
    public synchronized void setFloor(Floor floor){
        if(floor == mFloor || floor == null) return;
        mFloor = floor;

        for(ElevatorPositionListener listener : positionListeners){
            listener.onFloorChanged(floor);
        }
    }

    public synchronized void setOrderDirection(Direction direction){
        if(mOrderDirection == direction || direction == null) return;
        mOrderDirection = direction;

        for(ElevatorPositionListener listener : positionListeners){
            listener.onOrderDirectionChanged(direction);
        }
    }
    public synchronized Direction getOrderDirection(){
        return mOrderDirection;
    }

    public synchronized void setMotorDirection(Direction direction) {
        if(direction == mMotorDirection || direction == null) return;
        mMotorDirection =  direction;

        for(ElevatorPositionListener listener : positionListeners){
            listener.onMotorDirectionChanged(direction);
        }
    }
    public Direction getMotorDirection() {
        return mMotorDirection;
    }

    public synchronized void setDoor(boolean open){
        if(mDoorOpen.get() == open) return;
        mDoorOpen.set(open);

        for(ElevatorPositionListener listener : positionListeners){
            listener.onDoorOpenChanged(open);
        }
    }

    public boolean isDoorOpen(){
        return mDoorOpen.get();
    }

    /**************************************************************************************************************
     * Internal orders
     **************************************************************************************************************/

    public Order getInternalOrder(Floor floor){
        if(floor == null) return null;
        synchronized (internalOrders) {
            return internalOrders[floor.index];
        }
    }
    void addInternalOrder(Floor floor){
        if(floor == null) return;
        Order order = new Order(this, Button.INTERNAL, floor);
        synchronized (internalOrders){
            internalOrders[floor.index] = order;
        }
        for (OrderListener listener : orderListeners){
            listener.onOrderAdded(order);
        }
    }
    void clearInternalOrder(Floor floor){
        if (floor == null) return;
        Order order;
        synchronized (internalOrders){
            order = internalOrders[floor.index];
            if(order == null) return;
            internalOrders[floor.index] = null;
        }
        for (OrderListener listener : orderListeners){
            listener.onOrderRemoved(order);
        }
    }
    public Order[] getAllInternalOrders(){
        synchronized (internalOrders) {
            Order[] internalOrdersCopy = internalOrders.clone();
            return internalOrdersCopy;
        }
    }
    /*************************************************
     * Status - Connection, Operable
     *************************************************/

    public boolean isConnected(){
        return mConnected.get();
    }
    public void setConnected(boolean connected){
        if(connected == mConnected.get()) return;
        mConnected.set(connected);
        Log.i(this, "Elevator connection status changed. " + this + " is now changed to" + (connected? "connected" : "disconnected"));
        for(ElevatorStatusListener listener : statusListeners){
            listener.onConnectionChanged(this, connected);
        }
    }

    public boolean isOperable() {
        return mOperable.get();
    }
    public void setOperable(boolean operable) {
        if (mOperable.get() == operable) return;
        mOperable.set(operable);
        Log.i(this, "Operable status changed. Elevator is now "+ (operable? "operable" : "non-operable"));
        for (ElevatorStatusListener listener : statusListeners){
            listener.onOperableChanged(this, operable);
        }
    }

    /************************************************************
     * Identifiers:
     ************************************************************/
    public InetAddress getAddress(){
        return mInetAddress;
    }
    public boolean isLocal(){
        return SystemData.get() != null && this == SystemData.get().getLocalElevator();
    }
    public int getID(){ return mID;}

    @Override
    public String toString(){
        return isLocal() ? "E(Local)" : "E"+mID+"("+ getAddress().getHostAddress()+")";
    }
}
