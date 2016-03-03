package com.gruppe78;

import com.gruppe78.model.Button;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;
import com.gruppe78.model.ModelChangedListener;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ModelChangedListener {
    private static ElevatorController sElevatorController;

    private ElevatorController(){
        Model model = Model.get();
        model.addModelChangedListener(this);
    }

    public static ElevatorController get(){
        if(sElevatorController == null){
            sElevatorController = new ElevatorController();
        }
        return sElevatorController;
    }

    @Override
    public void onFloorChanged(Floor newFloor) {

    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {

    }
}
