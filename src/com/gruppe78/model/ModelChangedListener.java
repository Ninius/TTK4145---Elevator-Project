package com.gruppe78.model;

/**
 * Created by student on 12.02.16.
 */
public interface ModelChangedListener {
    void onFloorChanged(Floor newFloor);
    void onButtonPressed(Floor floor, Button button, boolean newValue);
}
