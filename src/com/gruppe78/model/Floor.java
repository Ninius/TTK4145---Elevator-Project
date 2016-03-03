package com.gruppe78.model;

public enum Floor {
    FLOOR0(0),
    FLOOR1(1),
    FLOOR2(2),
    FLOOR3(3);

    public static final int NUMBER_OF_FLOORS = Floor.values().length;
    public int index;

    Floor(int index){
        this.index = index;
    }
    public boolean isBottom(){
        return index == 0;
    }
    public boolean isTop(){
        return index + 1 == NUMBER_OF_FLOORS;
    }
}
