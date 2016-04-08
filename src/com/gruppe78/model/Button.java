package com.gruppe78.model;

public enum Button{
    OUTSIDE_UP(0),
    OUTSIDE_DOWN(1),
    INTERNAL(2);
    public final static int NUMBER_OF_BUTTONS = Button.values().length;
    public final int index;
    Button(int index){
        this.index = index;}
    public int getDirection(){
        int direction = 0;
        switch(index){
            case 0:
                direction = 1;
            case 1:
                direction = -1;
            case 2:
                direction = 0;
        }
        return direction;
    }
}
