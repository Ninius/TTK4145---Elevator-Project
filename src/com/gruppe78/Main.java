package com.gruppe78;

import com.gruppe78.driver.DriverController;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.Utilities;


import java.net.InetAddress;
import java.util.ArrayList;

/***************************************************************************************************
 * System description:
 * - The backbone of the system is the data objects, which
 *
 ***************************************************************************************************/

public class Main {
    private static final String NAME = Main.class.getSimpleName();

    //System settings:
    public static final String SYSTEM_ADDRESS_PREFIX = "129";
    private static final String[] ELEVATOR_ADDRESS_LIST = new String[]{};

    //References to components to prevent them from being garbage collected.
    private static ConnectedManager connectedManager;
    private static SystemData systemData;
      //private static AliveManager aliveManager;

    public static void main(String[] args) throws InterruptedException {
        Log.i(NAME, "System started");

        //Initializing driver
        boolean initialized = DriverHelper.init(DriverHelper.ELEVATOR_DRIVER);
        if(!initialized){
            Log.i(NAME, "Could not initialize the driver. System shutting down.");
            return;
        }

        //Retrieving the address of this machine. Maybe include a more safe/correct way to decide if connected?
        InetAddress localAddress = Utilities.getLocalAddress(SYSTEM_ADDRESS_PREFIX);
        while(localAddress == null){
            Log.i(NAME, "Could not retrieve the IP address of this machine. System sleeping until connected.");
            Thread.sleep(1000);
            localAddress = Utilities.getLocalAddress(SYSTEM_ADDRESS_PREFIX);
        }

        //Creating Elevator Objects for all the external elevators:
        ArrayList<Elevator> externalElevators = new ArrayList<>();
        for(String address : ELEVATOR_ADDRESS_LIST){
            InetAddress inetAddress = Utilities.getInetAddress(address);
            if(!inetAddress.equals(localAddress)) externalElevators.add(new Elevator(inetAddress));
        }

        //Initializing the system data:
        SystemData.init(externalElevators, new Elevator(localAddress));
        systemData = SystemData.get();

        connectedManager = ConnectedManager.get();
        connectedManager.start();
        DriverController.init();
        //aliveManager = AliveManager.get();
        //aliveManager.start();

        //Loading information on the system:
        LocalElevatorIOChecker.init();
        ElevatorController.init();
    }

    private static void initializeData(InetAddress localAddress){

    }
}
