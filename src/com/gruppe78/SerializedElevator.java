package com.gruppe78;

import com.gruppe78.model.*;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */

//getters not really required in current form.
public class SerializedElevator implements java.io.Serializable {
    private InetAddress mInetAddress;
    private SerializedOrder[] internalOrders = null; //Use SerializedOrder for single orders?
    private Floor mFloor = null;
    private Direction mOrderDirection = null;
    private Direction mMotorDirection = null;
    private Boolean mDoorOpen = null;
    private Boolean mConnected = null;
    private Boolean mOperable = null;

    public SerializedElevator(InetAddress InetAddress) {
        mInetAddress = InetAddress;
    }
    public SerializedElevator(Elevator elevator){
        mInetAddress = elevator.getAddress();
        int i = 0;
        internalOrders = new SerializedOrder[Floor.NUMBER_OF_FLOORS];
        for (Order order : elevator.getAllInternalOrders()){
            if (order != null) internalOrders[i] = new SerializedOrder(order, false);
            else internalOrders[i] = null;
            i++;
        }
        mFloor = elevator.getFloor();
        mOrderDirection = elevator.getOrderDirection();
        mMotorDirection = elevator.getMotorDirection();
        mDoorOpen = elevator.isDoorOpen();
        mConnected = elevator.isConnected();
        mOperable = elevator.isOperable();

    }
    public void updateElevator() {
        Elevator targetElevator = SystemData.get().getElevator(mInetAddress);
        if (internalOrders != null) {
            Floor floor = Floor.FLOOR0;
            for (SerializedOrder order : internalOrders) {
                if (order.getInetAddress() != null) targetElevator.addInternalOrder(order.getFloor(), order.getButton());
                else targetElevator.clearInternalOrder(floor);
                floor = floor.getNextFloor(Direction.UP);
            }
        }
        if (mFloor != null) {
            targetElevator.setFloor(mFloor);
        }
        if (mOrderDirection != null) {
            targetElevator.setOrderDirection(mOrderDirection);
        }
        if (mMotorDirection != null) {
            targetElevator.setMotorDirection(mMotorDirection);
        }
        if (mDoorOpen != null) {
            targetElevator.setDoor(mDoorOpen);
        }
        if (mConnected != null) {
            targetElevator.setConnected(mConnected);
        }
        if (mOperable != null) {
            targetElevator.setOperable(mOperable);
        }
        targetElevator.setUpToDate(true);
    }

    public Direction getmOrderDirection() {
        return mOrderDirection;
    }

    public void setmOrderDirection(Direction mOrderDirection) {
        this.mOrderDirection = mOrderDirection;
    }

    public Direction getmMotorDirection() {
        return mMotorDirection;
    }

    public void setmMotorDirection(Direction mMotorDirection) {
        this.mMotorDirection = mMotorDirection;
    }

    public boolean ismDoorOpen() {
        return mDoorOpen;
    }

    public void setmDoorOpen(boolean mDoorOpen) {
        this.mDoorOpen = mDoorOpen;
    }

    public boolean ismConnected() {
        return mConnected;
    }

    public void setmConnected(boolean mConnected) {
        this.mConnected = mConnected;
    }

    public boolean ismOperable() {
        return mOperable;
    }

    public void setmOperable(boolean mOperable) {
        this.mOperable = mOperable;
    }


}