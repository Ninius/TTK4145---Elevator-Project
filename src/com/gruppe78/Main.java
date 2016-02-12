package com.gruppe78;

import com.gruppe78.driver.Driver;
import com.gruppe78.driver.DriverHandler;

public class Main {
    public static void main(String[] args) {

        System.out.println("System started");
        DriverHandler.init(DriverHandler.DriverType.ELEVATOR);
        DriverHandler.setMotorDirection(DriverHandler.MotorDirection.UP);
    }

}
