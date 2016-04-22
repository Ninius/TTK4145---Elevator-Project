package com.gruppe78.model;

/**
 * Created by jespe on 10.04.2016.
 */
public interface ElevatorStatusListener {
    default void onConnectionChanged(Elevator elevator, boolean connected){}
    default void onOperableChanged(Elevator elevator, boolean operable){}
}
