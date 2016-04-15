package com.gruppe78;

import com.gruppe78.driver.DriverController;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.network.Networker;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.Utilities;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by jespe on 15.04.2016.
 */
public class MainAlt {
    private static final String NAME = Main.class.getSimpleName();

    //System settings:
    private static final String[] ELEVATOR_IP_LIST = new String[]{"129.241.124.98", "129.241.124.99"};
    private static final int PORT = 5000;
    private static final int CONNECT_TIMEOUT = 1000;

    //References to components to prevent them from being garbage collected.
    private static ConnectedManager connectedManager;
    private static SystemData systemData;
    private static Networker networker;
    //private static OperativeManager operativeManager;

    public static void main(String[] args) throws InterruptedException {
        Log.i(NAME, "System started");

        //Initializing driver
        boolean initialized = DriverHelper.init(DriverHelper.SIMULATOR_DRIVER);
        if (!initialized) {
            Log.i(NAME, "Could not initialize the driver. System exiting.");
            return;
        } else {
            Log.i(NAME, "Driver initialized");
        }

        //Creating elevator objects from IP-list:
        ArrayList<Elevator> elevators = new ArrayList<>();
        for (int i = 0; i < ELEVATOR_IP_LIST.length; i++) {
            InetAddress inetAddress = Utilities.getInetAddress(ELEVATOR_IP_LIST[i]);
            if (inetAddress == null)
                Log.e(NAME, "IP: " + ELEVATOR_IP_LIST[i] + " was not a valid IP-address. Removing it from the system.");
            else elevators.add(new Elevator(inetAddress, i));
        }

        //Find the elevator that is this machine / Waiting on connection.
        Elevator localElevator = Utilities.getConnectedElevator(elevators);
        if (localElevator == null)
            Log.i(NAME, "Local IP corresponding to the IP-list not found. Connection presumed to be down. Waiting...");
        while (localElevator == null) {
            Thread.sleep(100);
            localElevator = Utilities.getConnectedElevator(elevators);
        }
        Log.i(NAME, "The system is connected - Local address: " + localElevator.getAddress().getHostAddress());

        //Initializing the system data:
        SystemData.init(elevators, localElevator);
        systemData = SystemData.get();
        Log.i(NAME, "System Data initialized, elevators in the system: " + SystemData.get().getElevatorList());

        LocalElevatorInputChecker.get().start();
    }
}
