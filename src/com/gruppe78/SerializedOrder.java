package com.gruppe78;

import com.gruppe78.model.*;
import com.sun.xml.internal.ws.transport.http.server.ServerAdapter;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */
//First draft, please change if needed.
public class SerializedOrder implements java.io.Serializable {
    private final InetAddress mInetAddress;
    private final Floor mFloor;
    private final Button mButton;

    public Floor getFloor(){
        return mFloor;
    }
    public Button getButton(){
        return mButton;
    }
    public InetAddress getInetAddress(){
        return mInetAddress;
    }

    public SerializedOrder(Elevator elevator, Button button, Floor floor){
        mInetAddress = elevator.getInetAddress();
        mButton = button;
        mFloor = floor;
    }
    public Order getOrder() {
        return new Order(SystemData.get().getElevator(mInetAddress), getButton(), getFloor());
    }
    public void updateOrders(){
        if (mInetAddress == null) return;
        if (mButton == Button.INTERNAL){
            SystemData.get().getElevator(mInetAddress).addInternalOrder(mFloor, mButton);
        }
        else{
            SystemData.get().addGlobalOrder(getOrder());
        }
    }
}
