package com.gruppe78.driver;

import com.gruppe78.model.*;

/**
 * Controls all lights of the local system.
 */
public class LocalElevatorDriverBridge implements ElevatorPositionListener, OrderListener {
    private static LocalElevatorDriverBridge sLocalElevatorDriverBridge;

    private LocalElevatorDriverBridge(){}

    public static LocalElevatorDriverBridge get(){
        if(sLocalElevatorDriverBridge == null){
            sLocalElevatorDriverBridge = new LocalElevatorDriverBridge();
        }
        return sLocalElevatorDriverBridge;
    }

    public void init(){
        Elevator elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorMovementListener(this);
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
