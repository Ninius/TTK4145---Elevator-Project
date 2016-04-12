package com.gruppe78.model;

/**
 * Created by student on 04.03.16.
 */
public class Order {
    private final Elevator mOrderElevator;
    private final Floor mFloor;
    private final Button mType;

    Order(Elevator orderElevator, Button buttonType, Floor floor){
        mOrderElevator = orderElevator;
        mType = buttonType;
        mFloor = floor;
    }

    public Elevator getElevator(){
        return mOrderElevator;
    }
    public Floor getFloor(){
        return mFloor;
    }
    public Button getButton(){
        return mType;
    }
}
