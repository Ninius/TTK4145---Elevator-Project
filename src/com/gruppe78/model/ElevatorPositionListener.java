package com.gruppe78.model;

/**
 * Created by student on 12.02.16.
 */
public interface ElevatorPositionListener {
    void onFloorChanged(Floor newFloor);
    void onMotorDirectionChanged(Direction newDirection);
    void onOrderDirectionChanged(Direction newDirection);
    void onDoorOpenChanged(boolean newOpen);
}
