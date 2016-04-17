package com.gruppe78;

import com.gruppe78.model.*;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */
//First draft, please change if needed.
public class SerializedOrder implements java.io.Serializable {
    private InetAddress mInetAddress;
    private Floor mFloor;
    private Button mButton;
    private boolean mClearOrder;
    public Floor getFloor(){
        return mFloor;
    }
    
    public Button getButton(){
        return mButton;
    }
    public InetAddress getInetAddress(){
        return mInetAddress;
    }

    public SerializedOrder(Order order, boolean clearOrder){
        if (order != null){
            mInetAddress = order.getElevator().getAddress();
            mButton = order.getButton();
            mFloor = order.getFloor();
            mClearOrder = clearOrder;

        }
    }
    public Order getOrder() {
        return new Order(SystemData.get().getElevator(mInetAddress), mButton, mFloor);
    }
    public void updateOrders(){
        if (mInetAddress == null) return;
        if (mButton == Button.INTERNAL){
            if (mClearOrder) SystemData.get().getElevator(mInetAddress).clearInternalOrder(mFloor);
            else SystemData.get().getElevator(mInetAddress).addInternalOrder(mFloor, mButton);
        }
        else{
            if (mClearOrder) SystemData.get().clearGlobalOrder(mFloor, mButton.isUp());
            else SystemData.get().addGlobalOrder(getOrder());
        }
    }
}
