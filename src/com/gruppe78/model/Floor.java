package com.gruppe78.model;

/**
 * Created by student on 26.02.16.
 */
public enum Floor {
    FLOOR0(0),
    FLOOR1(1),
    FLOOR2(2),
    FLOOR3(3);
    private static final int topFloor = 3;
    public int index;
    Floor(int index){
        this.index = index;
    }
    public boolean isBottom(){
        return index == 0;
    }
    public boolean isTop(){
        return index == topFloor;
    }
}
