package com.gruppe78;

import com.gruppe78.model.*;

/**
 * Created by jespe on 10.04.2016.
 */
public class OrderHandler implements ElevatorEventListener{
    private static OrderHandler sOrderHandler;

    private OrderHandler(){
        SystemData.get().getLocalElevator().addElevatorEventListener(this);
    }
    public static void init(){
        if(sOrderHandler != null) return;
        sOrderHandler = new OrderHandler();
    }
    public static OrderHandler get(){
        return sOrderHandler;
    }


    @Override
    public void onFloorChanged(Floor newFloor) {

    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {

    }
}
