package com.gruppe78.driver;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

/**
 * Controls all lights of the local system.
 */
public class DriverController implements ElevatorPositionListener, OrderListener {
    private static DriverController sDriverController;

    private DriverController(){
        Elevator elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorMovementListener(this);
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
        if(newFloor != null) DriverHelper.setFloorIndicator(newFloor);
    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection) {
        DriverHelper.setMotorDirection(newDirection);
    }

    @Override
    public void onOrderDirectionChanged(Direction newDirection) {
        //Ignore
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
        Log.i(this, "Remove light" + order.getFloor()+ order.getButton());
        DriverHelper.setButtonLamp(order.getButton(), order.getFloor(), false);
    }
}
