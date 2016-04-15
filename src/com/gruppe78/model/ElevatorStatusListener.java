package com.gruppe78.model;

/**
 * Created by jespe on 10.04.2016.
 */
public interface ElevatorStatusListener {
    void onConnectionChanged(boolean connected);
    void onOperableChanged(boolean operable);
}
