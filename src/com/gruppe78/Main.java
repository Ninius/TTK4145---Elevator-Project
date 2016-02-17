package com.gruppe78;

import com.gruppe78.driver.DriverHandler;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("System started");
        DriverHandler.init(DriverHandler.DriverType.SIMULATOR);
        DriverHandler.setDoorOpenLamp(true);
        DriverHandler.setFloorIndicator(3);
        while (true){
            Thread.sleep(2000);
            System.out.println("Is stop pressed: "+DriverHandler.isStopPressed());
        }

        //DriverHandler.setMotorDirection(DriverHandler.MotorDirection.UP);
    }

}
