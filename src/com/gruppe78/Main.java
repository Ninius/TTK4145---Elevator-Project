package com.gruppe78;

import com.gruppe78.driver.DriverController;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.Utilities;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/***************************************************************************************************
 * System description:
 * - The backbone of the system is the data objects, which
 *
 ***************************************************************************************************/

public class Main {
    private static final String NAME = Main.class.getSimpleName();

    //System settings:
    public static final String SYSTEM_ADDRESS_PREFIX = "129";
    private static final String[] ELEVATOR_IP_LIST = new String[]{"129.241.124.98", "129.241.124.97"};

    //References to components to prevent them from being garbage collected.
    private static ConnectedManager connectedManager;
    private static SystemData systemData;
      //private static OperativeManager operativeManager;

    public static void main(String[] args) throws InterruptedException {
        Log.i(NAME, "System started");

        //Initializing driver
        boolean initialized = DriverHelper.init(DriverHelper.SIMULATOR_DRIVER);
        if(!initialized){
            Log.i(NAME, "Could not initialize the driver. System exiting.");
            return;
        }else{
            Log.i(NAME, "Driver initialized");
        }


        ArrayList<Elevator> elevators = new ArrayList<>();
        for(String IP : ELEVATOR_IP_LIST){
            InetAddress inetAddress = Utilities.getInetAddress(IP);
            if(inetAddress == null) Log.e(NAME, "IP: "+IP+" was not a valid IP-address. Removing it from the system.");
            else elevators.add(new Elevator(inetAddress));
        }


        Log.i(NAME, "Finding elevator that has IP-address connecting this machine...");
        Elevator localElevator = Utilities.getConnectedElevator(elevators);
        while(localElevator == null){
            Thread.sleep(100);
            localElevator = Utilities.getConnectedElevator(elevators);
        }
        Log.i(NAME, "... This system is connected - Address found: " + localElevator.getInetAddress().getHostAddress());

        //Initializing the system data:
        SystemData.init(elevators, localElevator);
        systemData = SystemData.get();
        Log.i(NAME, "System Data initialized");

        //connectedManager = ConnectedManager.get();
        //connectedManager.start();
        //DriverController.init();
        //operativeManager = OperativeManager.get();
        //operativeManager.start();

        //Loading information on the system:
        //LocalElevatorInputChecker.get().start();
        //ElevatorController.init();
    }

    private static void initializeData(InetAddress localAddress){

    }
}
