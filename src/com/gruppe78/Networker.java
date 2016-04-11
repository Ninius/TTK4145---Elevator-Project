package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;

import java.net.InetAddress;


public class Networker {
    public static void establishConnections(){
        for(Elevator elevator : SystemData.get().getElevatorList()){
            InetAddress address = elevator.getInetAddress();
        }
    }
}
