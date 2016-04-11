package com.gruppe78.model;

/**
 * Created by jespe on 25.02.2016.
 */
public enum Direction {
    UP(1),
    NONE(0),
    DOWN(-1);
    public final int directionIndex;
    Direction(int index){directionIndex = index;}

}
