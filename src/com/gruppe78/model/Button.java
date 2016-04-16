package com.gruppe78.model;

public enum Button{
    OUTSIDE_UP(0, true, false),
    OUTSIDE_DOWN(1, false, true),
    INTERNAL(2, true, true);

    public final static int NUMBER_OF_BUTTONS = Button.values().length;

    public final int index;
    public final boolean up;
    public final boolean down;
    Button(int index, boolean up, boolean down){
        this.index = index;
        this.up = up;
        this.down = down;
    }

    public boolean matchDirection(Direction direction){
        if(direction == Direction.DOWN) return down;
        if(direction == Direction.UP) return up;
        return true;
    }
    public boolean isUp(){
        return up;
    }
    public boolean isDown(){
        return down;
    }
    public Direction getDirection(){
        if (up) return Direction.UP;
        else if (down) return Direction.DOWN;
        else return Direction.NONE;
    }
}
