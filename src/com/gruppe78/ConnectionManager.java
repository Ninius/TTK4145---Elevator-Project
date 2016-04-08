package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.Model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.List;

/**
 * Created by student on 08.04.16.
 */
public class ConnectionManager extends Thread{

    @Override
    public void run() {
        List<Elevator> elevators = Model.get().getElevatorList();
        for(Elevator elevator : elevators){
            String address = elevator.getIPAddress();
        }
    }
}
