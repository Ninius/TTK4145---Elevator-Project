package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.*;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ElevatorEventListener {
    private static ElevatorController sElevatorController;
    private Elevator localElevator;

    private ElevatorController(Elevator elevator){
        localElevator = elevator;
        localElevator.addElevatorEventListener(this);
    }

    public static void init(Elevator elevator){
        if(sElevatorController != null) return;
        sElevatorController = new ElevatorController(elevator);
    }

    public static ElevatorController get(){
        return sElevatorController;
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null){
            DriverHandler.setFloorIndicator(newFloor);
        }
        /*if (Model.get().getOrder(newFloor) == true){
            DriverHandler.setMotorDirection(MotorDirection.STOP);

        }*/
    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {
        DriverHandler.setButtonLamp(button, floor, true);
    }
}
