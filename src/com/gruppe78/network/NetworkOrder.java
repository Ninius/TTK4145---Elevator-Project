package com.gruppe78.network;

import com.gruppe78.model.*;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */
//First draft, please change if needed.
public class NetworkOrder implements java.io.Serializable {
    private final InetAddress orderElevatorAddress;
    private final Floor floor;
    private final Button button;

    public NetworkOrder(Order order){
        orderElevatorAddress = order.getElevator().getAddress();
        button = order.getButton();
        floor = order.getFloor();
    }
    public Order getOrder(SystemData data) {
        return new Order(data.getElevator(orderElevatorAddress), button, floor);
    }
    /*
    public void updateOrders(){
        if (mInetAddress == null) return;
        if (mButton == Button.INTERNAL){
            SystemData.get().getElevator(mInetAddress).addInternalOrder(mFloor, mButton);
        }
        else{
            SystemData.get().addGlobalOrder(getOrder());
        }
    }*/
}
