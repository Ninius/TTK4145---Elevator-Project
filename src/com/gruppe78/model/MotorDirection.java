package com.gruppe78.model;

/**
 * Created by jespe on 25.02.2016.
 */
public enum MotorDirection{
    UP(1),
    STOP(0),
    DOWN(-1);
    public final int directionIndex;
    MotorDirection(int index){directionIndex = index;}
}
