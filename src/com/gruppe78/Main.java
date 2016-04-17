package com.gruppe78;

import com.gruppe78.driver.DriverLocalElevatorBridge;
import com.gruppe78.driver.LocalElevatorDriverBridge;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.network.Networker;
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
    private static final String[] ELEVATOR_IP_LIST = new String[]{"127.0.0.1"};
    private static final int PORT = 1000;
    private static final int CONNECT_TIMEOUT = 5000;

    //References to components to prevent them from being garbage collected.
    private static SystemData systemData;
    private static Networker networker;
    private static LocalElevatorDriverBridge localElevatorDriverBridge;
    private static DriverLocalElevatorBridge driverLocalElevatorBridge;
    private static ElevatorController elevatorController;
    private static OperativeManager operativeManager;
    private static OrderHandler orderHandler;

    public static void main(String[] args) throws InterruptedException {
        Log.i(NAME, "System started");

        //Initializing driver
        try {
            DriverHelper.init(DriverHelper.SIMULATOR_DRIVER);
            Log.i(NAME, "Driver initialized.");

            //Creating elevator objects from IP-list:
            ArrayList<Elevator> elevators = new ArrayList<>();
            for(int i = 0; i < ELEVATOR_IP_LIST.length; i++){
                InetAddress inetAddress = Utilities.getInetAddress(ELEVATOR_IP_LIST[i]);
                if(inetAddress == null) Log.e(NAME, "IP: "+ELEVATOR_IP_LIST[i]+" was not a valid IP-address. Removing it from the system.");
                else elevators.add(new Elevator(inetAddress, i));
            }

            //Find the elevator that is this machine / Waiting on connection.
            Elevator localElevator = Utilities.getConnectedElevator(elevators);
            if(localElevator == null) Log.i(NAME, "Local IP corresponding to the IP-list not found. Connection presumed to be down. Waiting...");
            while(localElevator == null){
                Thread.sleep(100);
                localElevator = Utilities.getConnectedElevator(elevators);
            }
            Log.i(NAME, "The system is connected - Local address: " + localElevator.getAddress().getHostAddress());

            //Initializing the system data:
            SystemData.init(elevators, localElevator);
            systemData = SystemData.get();
            Log.i(NAME, "System Data initialized, elevators in the system: "+SystemData.get().getElevatorList());

            //Establishing connections:
            networker = Networker.get();
            networker.createConnections(PORT, CONNECT_TIMEOUT);

            //Connecting system model of the elevator to the physical elevator.
            localElevatorDriverBridge = LocalElevatorDriverBridge.get();
            localElevatorDriverBridge.init();

            driverLocalElevatorBridge = DriverLocalElevatorBridge.get();
            driverLocalElevatorBridge.start();

            operativeManager = OperativeManager.get();
            operativeManager.start();

            initiateLocalElevatorPosition(localElevator);
        } catch (Exception e) {
            Log.s(NAME, e);
            return;
        }
    }

    private static void initiateLocalElevatorPosition(Elevator localElevator){
        Log.i(NAME, "Initializing elevator to a floor.");
        if(localElevator.getFloor() != null){
            Main.onLocalElevatorPositionInitialized();
        }else{
            ElevatorPositionListener elevatorFloorListener = new ElevatorPositionListener() {
                @Override public void onFloorChanged(Floor newFloor) {
                    SystemData.get().getLocalElevator().removeElevatorMovementListener(this);
                    SystemData.get().getLocalElevator().setMotorDirection(Direction.NONE);
                    Log.i(NAME, "Elevator is initialized to a floor.");
                    Main.onLocalElevatorPositionInitialized();
                }
            };
            localElevator.addElevatorPositionListener(elevatorFloorListener);
            localElevator.setMotorDirection(Direction.DOWN);
        }
    }

    private static void onLocalElevatorPositionInitialized(){
        orderHandler = OrderHandler.get();

        elevatorController = ElevatorController.get();
        elevatorController.init();
    }

}
