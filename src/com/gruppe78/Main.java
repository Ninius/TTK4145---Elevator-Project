package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;
import com.gruppe78.model.MotorDirection;

public class Main {
    private static Elevator localElevator;
    public static void main(String[] args) throws InterruptedException {
        System.out.println("System initializing");
        DriverHandler.init(DriverHandler.ELEVATOR_DRIVER);
        localElevator = new Elevator(true);
        Model.get().addElevator(localElevator);

        ElevatorEventHandler.init(localElevator);
        ElevatorController.init(localElevator);
    }
}
