package com.gruppe78.model;

/**
 * Created by jespe on 25.02.2016.
 */
public enum Button{
    OUTSIDE_UP(0),
    OUTSIDE_DOWN(1),
    INTERNAL(2);
    public final int buttonIndex;
    Button(int index){buttonIndex = index;}
}
