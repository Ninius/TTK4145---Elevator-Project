package com.gruppe78.model;

/**
 * Created by jespe on 10.04.2016.
 */
public interface ElevatorStatusListener {
    void onConnectionChanged(Elevator elevator, boolean connected);
    void onOperableChanged(Elevator elevator, boolean operable);
}
