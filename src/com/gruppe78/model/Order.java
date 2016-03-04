package com.gruppe78.model;

/**
 * Created by student on 04.03.16.
 */
public class Order {
    private Elevator mOrderElevator;
    private Floor mFloor;
    private Button mType;

    Order(Elevator orderElevator, Button buttonType, Floor mFloor){
        mOrderElevator = orderElevator;
        mType = buttonType;
    }

    public Elevator getElevator(){
        return mOrderElevator;
    }
    public Floor getFloor(){
        return mFloor;
    }
    public Button getType(){
        return mType;
    }
}
