package com.gruppe78;

import com.gruppe78.model.*;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */

//getters not really required in current form.
public class SerializedElevator implements java.io.Serializable {
    private final InetAddress mInetAddress;
    private SerializedOrder[] internalOrders = null; //Use SerializedOrder for single orders?
    private Floor mFloor = null;
    private Direction mOrderDirection = null;
    private Direction mMotorDirection = null;
    private Boolean mDoorOpen = null;
    private Boolean mConnected = null;
    private Boolean mOperable = null;
    private Long lastConnect = null;

    public SerializedElevator(InetAddress InetAddress) {
        mInetAddress = InetAddress;
    }

    public void updateElevator() {
        Elevator targetElevator = SystemData.get().getElevator(mInetAddress);
        if (internalOrders != null) {
            for (SerializedOrder order : internalOrders) {
                targetElevator.addInternalOrder(order.getFloor(), order.getButton());
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

    public long getLastConnect() {
        return lastConnect;
    }

    public void setLastConnect(long lastConnect) {
        this.lastConnect = lastConnect;
    }


}