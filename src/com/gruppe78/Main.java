package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.MotorDirection;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("System started");
        DriverHandler.init(DriverHandler.SIMULATOR_DRIVER);
        DriverHandler.setDoorOpenLamp(true);
        DriverHandler.setMotorDirection(MotorDirection.UP);
        while (true){
            Thread.sleep(1000);
            if(DriverHandler.getElevatorFloor() == 3){
                DriverHandler.setMotorDirection(MotorDirection.STOP);
            }
        }
    }

}
