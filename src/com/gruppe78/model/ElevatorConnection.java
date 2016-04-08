package com.gruppe78.model;

/**
 * Created by student on 08.04.16.
 */
public class ElevatorConnection {
    private String mIPAddress;
    private boolean mConnected = false;

    ElevatorConnection(String IPAddress){
        mIPAddress = IPAddress;
    }
}
