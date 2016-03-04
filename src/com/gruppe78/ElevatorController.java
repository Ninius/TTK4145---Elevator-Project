package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.*;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ModelChangedListener {
    private static ElevatorController sElevatorController;
    private Model mModel;

    private ElevatorController(){
        mModel = Model.get();
        mModel.addModelChangedListener(this);
    }

    public static ElevatorController get(){
        if(sElevatorController == null){
            sElevatorController = new ElevatorController();
        }
        return sElevatorController;
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null){
            DriverHandler.setFloorIndicator(newFloor);
        }
        if (mModel.getOrder(newFloor) == true){
            DriverHandler.setMotorDirection(MotorDirection.STOP);

        }
    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {
        DriverHandler.setButtonLamp(button, floor, true);
    }
}
