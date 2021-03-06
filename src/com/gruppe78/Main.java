package com.gruppe78;

import com.gruppe78.driver.DriverToLocalElevator;
import com.gruppe78.driver.LocalElevatorToDriver;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.network.NetworkMessenger;
import com.gruppe78.network.NetworkStarter;
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
    private static String[] ELEVATOR_IP_LIST = new String[]{"129.241.187.142", "129.241.187.144", "129.241.187.152"};
    private static final int PORT = 6251;
    private static final int CONNECT_TIMEOUT = 5000;

    //References to components to prevent them from being garbage collected.
    private static NetworkStarter networkStarter;
    private static LocalElevatorToDriver localElevatorToDriver;
    private static DriverToLocalElevator driverToLocalElevator;
    private static ElevatorController elevatorController;
    private static OperativeManager operativeManager;
    private static OrderHandler orderHandler;

    public static void main(String[] args) throws InterruptedException {
        Log.i(NAME, "System started");

        try {
            if(args.length > 0) ELEVATOR_IP_LIST = args;

            //Initializing driver
            DriverHelper.init(DriverHelper.ELEVATOR_DRIVER);
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
            localElevator.setConnected(true);

            //Initializing the system data:
            SystemData.init(elevators, localElevator);
            Log.i(NAME, "System Data initialized, elevators in the system: "+SystemData.get().getElevatorList());

            //Sending data to other elevators when states changes:
            NetworkMessenger.get().initBroadcasting();

            //Establishing connections:
            networkStarter = NetworkStarter.get();
            networkStarter.createElevatorConnections(CONNECT_TIMEOUT, PORT);

            //Connecting system model of the elevator to the physical elevator.
            localElevatorToDriver = LocalElevatorToDriver.get();
            localElevatorToDriver.init();

            driverToLocalElevator = DriverToLocalElevator.get();
            driverToLocalElevator.startFloorCheck();

            //Monitoring that the physical elevator is responding
            operativeManager = OperativeManager.get();
            operativeManager.start();

            //Monitoring status of elevators and reassigning orders.
            OrderHandler.get().init();

            initiateLocalElevatorPosition(localElevator);
        } catch (Exception e) {
            Log.s(NAME, e);
        }
    }

    private static void initiateLocalElevatorPosition(Elevator localElevator){
        Log.i(NAME, "Initializing elevator to a floor.");
        if(localElevator.getFloor() != null){
            localElevator.setMotorDirection(Direction.NONE);
            Main.onLocalElevatorPositionInitialized();
        }else{
            ElevatorPositionListener elevatorFloorListener = new ElevatorPositionListener() {
                @Override public void onFloorChanged(Floor newFloor) {
                    SystemData.get().getLocalElevator().removeElevatorMovementListener(this);
                    SystemData.get().getLocalElevator().setMotorDirection(Direction.NONE);
                    Main.onLocalElevatorPositionInitialized();
                }

                @Override public void onMotorDirectionChanged(Direction newDirection) {}
                @Override public void onOrderDirectionChanged(Direction newDirection) {}
                @Override public void onDoorOpenChanged(boolean newOpen) {}
            };
            localElevator.addElevatorPositionListener(elevatorFloorListener);
            localElevator.setMotorDirection(Direction.DOWN);
        }
    }

    private static void onLocalElevatorPositionInitialized(){
        Log.i(NAME, "Elevator is initialized to a floor.");
        elevatorController = ElevatorController.get();
        elevatorController.init();
        driverToLocalElevator.startButtonCheck();
    }

}
