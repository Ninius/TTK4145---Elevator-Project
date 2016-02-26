package com.gruppe78;

import com.gruppe78.model.Model;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ModelChangedListener{
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
    public int onFloorChanged() {
        return 0;
    }
}
