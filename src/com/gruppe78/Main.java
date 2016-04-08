package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;
import com.gruppe78.model.MotorDirection;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.Utilities;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Main {
    private static final String NAME = Main.class.getSimpleName();
    private static Elevator localElevator;


    public static void main(String[] args) throws InterruptedException {
        Log.i(Main.class.getSimpleName(), "System started");

        String localAddress = Utilities.getLocalAddress();
        if(localAddress == null){
            Log.i(NAME,"Could not get local address, system shutting down.");
        }

        DriverHandler.init(DriverHandler.ELEVATOR_DRIVER);

        localElevator = new Elevator(localAddress);
        Model.get().addElevator(localElevator);

        ElevatorEventHandler.init(localElevator);
        ElevatorController.init(localElevator);
    }
}
