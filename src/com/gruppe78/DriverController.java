package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.*;

/**
 * Controls all lights of the local system.
 */
public class DriverController implements ElevatorPositionListener, OrderEventListener {
    private static DriverController sDriverController;
    private final Elevator elevator;

    private DriverController(){
        elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorEventListener(this);
        elevator.addOrderEventListener(this);
        SystemData.get().addOrderEventListener(this);
    }

    public static void init(){
        if(sDriverController != null) return;
        sDriverController = new DriverController();
    }

    public static DriverController get(){
        return sDriverController;
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null) DriverHandler.setFloorIndicator(newFloor);
    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection) {
        DriverHandler.setMotorDirection(newDirection);
    }

    @Override
    public void onOrderDirectionChanged(Direction newDirection) {
        //Ignore
    }

    @Override
    public void onDoorOpenChanged(boolean newOpen) {
        DriverHandler.setDoorOpenLamp(newOpen);
    }

    @Override
    public void onOrderAdded(Order order) {
        DriverHandler.setButtonLamp(order.getButton(), order.getFloor(), true);
    }

    @Override
    public void onOrderRemoved(Order order) {
        DriverHandler.setButtonLamp(order.getButton(), order.getFloor(), false);
    }
}
