package com.gruppe78.model;

public enum Button{
    OUTSIDE_UP(0),
    OUTSIDE_DOWN(1),
    INTERNAL(2);
    public final static int NUMBER_OF_BUTTONS = Button.values().length;
    public final int buttonIndex;
    Button(int index){buttonIndex = index;}
}
