package com.gruppe78.utilities;

import com.gruppe78.model.Elevator;

import java.net.*;
import java.util.ArrayList;

public class Utilities {
    private static final String NAME = Utilities.class.getSimpleName();

    public static InetAddress getInetAddress(String IP){
        try {
            return InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            Log.i(NAME,"Invalid IP was given");
        }
        return null;
    }
    public static Elevator getConnectedElevator(ArrayList<Elevator> elevators){
        for(Elevator elevator : elevators){
            if(isElevatorLocalConnected(elevator)) return elevator;
        }
        return null;
    }
    public static boolean isElevatorLocalConnected(Elevator elevator){
        try {
            return NetworkInterface.getByInetAddress(elevator.getAddress()) != null;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }
}
