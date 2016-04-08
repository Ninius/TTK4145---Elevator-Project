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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Main {
    private static final String NAME = Main.class.getSimpleName();
    private static final String[] elevatorAddressList = new String[]{};

    private ConnectionManager connectionManager;


    public static void main(String[] args) throws InterruptedException {
        Log.i(Main.class.getSimpleName(), "System started");

        DriverHandler.init(DriverHandler.ELEVATOR_DRIVER);

        ArrayList<Elevator> elevators = new ArrayList<>();

        String localAddress = Utilities.getLocalAddress();
        if(localAddress == null){
            Log.i(NAME,"Could not get local address, system shutting down.");
            return;
        }
        Elevator localElevator = new Elevator(localAddress);
        elevators.add(localElevator);

        //Initialize potential other elevators:
        for(String address : elevatorAddressList){
            elevators.add(new Elevator(address));
        }


        Model.init(elevators);


        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.start();

        //Loading information on the system:
        ElevatorEventHandler.init(localElevator);
        ElevatorController.init(localElevator);
    }
}
