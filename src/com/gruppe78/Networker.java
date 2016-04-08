package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.Model;

import java.util.ArrayList;


public class Networker {
    public static void establishConnections(){
        for(Elevator elevator : Model.get().getElevatorList()){
            String address = elevator.getAddress();
        }
    }
}
