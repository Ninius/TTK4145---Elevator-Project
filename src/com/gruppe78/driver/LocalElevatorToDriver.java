package com.gruppe78.driver;

import com.gruppe78.model.*;

/**
 * Controls all lights of the local system.
 */
public class LocalElevatorToDriver implements ElevatorPositionListener, OrderListener {
    private static LocalElevatorToDriver sLocalElevatorToDriver;

    private LocalElevatorToDriver(){}

    public static LocalElevatorToDriver get(){
        if(sLocalElevatorToDriver == null){
            sLocalElevatorToDriver = new LocalElevatorToDriver();
        }
        return sLocalElevatorToDriver;
    }

    public void init(){
        Elevator elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorPositionListener(this);
        elevator.addOrderEventListener(this);
        SystemData.get().addOrderEventListener(this);
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null) DriverHelper.setFloorIndicator(newFloor);
    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection) {
        DriverHelper.setMotorDirection(newDirection);
    }

    @Override
    public void onDoorOpenChanged(boolean newOpen) {
        DriverHelper.setDoorOpenLamp(newOpen);
    }

    @Override
    public void onOrderAdded(Order order) {
        DriverHelper.setButtonLamp(order.getButton(), order.getFloor(), true);
    }

    @Override
    public void onOrderRemoved(Order order) {
        DriverHelper.setButtonLamp(order.getButton(), order.getFloor(), false);
    }
}
