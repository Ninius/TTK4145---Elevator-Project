package com.gruppe78.model;

/**
 * Created by student on 04.03.16.
 */
public class Order {
    private final Elevator orderElevator;
    private final Floor floor;
    private final Button button;

    public Order(Elevator orderElevator, Button button, Floor floor){
        this.orderElevator = orderElevator;
        this.button = button;
        this.floor = floor;
    }

    public Elevator getElevator(){
        return orderElevator;
    }
    public Floor getFloor(){
        return floor;
    }
    public Button getButton(){
        return button;
    }

    @Override
    public String toString(){
        return "O(F:"+ floor.index +", B:"+ button + ", "+ orderElevator +")";
    }
}
