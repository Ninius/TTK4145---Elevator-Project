package com.gruppe78.model;

/**
 * Created by student on 04.03.16.
 */
public class Order {
    private final Elevator mOrderElevator;
    private final Floor mFloor;
    private final Button mButton;

    public Order(Elevator orderElevator, Button button, Floor floor){
        mOrderElevator = orderElevator;
        mButton = button;
        mFloor = floor;
    }

    public Elevator getElevator(){
        return mOrderElevator;
    }
    public Floor getFloor(){
        return mFloor;
    }
    public Button getButton(){
        return mButton;
    }


    @Override
    public String toString(){
        return "O(F:"+mFloor.index +", B:"+mButton + ", "+mOrderElevator+")";
    }
}
