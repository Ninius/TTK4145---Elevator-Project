package com.gruppe78.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public Direction directionTo(Floor floor){
        if(floor == null) return Direction.NONE;
        int diff = floor.index - index;
        if(diff > 0) return Direction.UP;
        if(diff < 0) return Direction.DOWN;
        return Direction.NONE;
    }
    public int lengthTo(Floor floor){
        if(floor == null) return NUMBER_OF_FLOORS;
        int diff = floor.index - index;
        return diff > 0 ? diff : -diff;
    }
    public Floor getNextFloor(Direction direction){
        if(direction == Direction.NONE) return null;
        if(direction == Direction.UP && this.isTop()) return null;
        if(direction == Direction.DOWN && this.isBottom()) return null;
        return Floor.values()[this.index + direction.directionIndex];
    }
}
