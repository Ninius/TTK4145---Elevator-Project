package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;
import com.gruppe78.model.MotorDirection;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.Utilities;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Main {
    //Predefined system variables:
    private static final int DISCOVERY_PORT = 4123;
    private static final int DISCOVER_TIMEOUT = 5000; //ms


    private static Elevator localElevator;
    public static void main(String[] args) throws InterruptedException {
        Log.i(Main.class.getSimpleName(), "System started");
        DeviceSearcher.get().searchForElevators(DISCOVERY_PORT, DISCOVER_TIMEOUT);
        DeviceSearcher.get().startElevatorSearchServer(DISCOVERY_PORT);

        /*DriverHandler.init(DriverHandler.ELEVATOR_DRIVER);
        localElevator = new Elevator(true);
        Model.get().addElevator(localElevator);

        ElevatorEventHandler.init(localElevator);
        ElevatorController.init(localElevator);*/
    }
}
