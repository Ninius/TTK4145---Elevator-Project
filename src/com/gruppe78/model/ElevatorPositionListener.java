package com.gruppe78.model;

/**
 * Created by student on 12.02.16.
 */
public interface ElevatorPositionListener {
    default void onFloorChanged(Floor newFloor){}
    default void onMotorDirectionChanged(Direction newDirection){};
    default void onOrderDirectionChanged(Direction newDirection){};
    default void onDoorOpenChanged(boolean newOpen){};
}
