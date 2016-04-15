package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;


public class Networker {
    private final InetAddress LOCAL_ADDRESS;
    private SystemData data;

    private static Networker sNetworker;

    private HashMap<InetAddress, ElevatorConnection> connections = new HashMap<>();
    private ConnectServer connectionServer;
    private HashMap<Elevator, ConnectClient> connectors = new HashMap<>();

    /*****************************************
     * Initialization
     *****************************************/

    private Networker(){
        data = SystemData.get();
        LOCAL_ADDRESS = data.getLocalElevator().getAddress();
        for(Elevator elevator : data.getElevatorList()){
            //if(elevator == data.getLocalElevator()) continue; //TODO: Comment in.
            connections.put(elevator.getAddress(), new ElevatorConnection(elevator));
        }
    }
    public static Networker get(){
        if(sNetworker == null){
            sNetworker = new Networker();
        }
        return sNetworker;
    }

    public void createConnections(int connectTimeout, int port) throws NetworkException {
        try {
            //Starting clients:
            Elevator localElevator = data.getLocalElevator();
            for(Elevator elevator : data.getElevatorList()) {
                if (localElevator.getID() < elevator.getID()) {
                    Log.i(this, "Establishment of connection to " + elevator + ": Client.");
                    if (connectors.get(elevator) != null) connectors.get(elevator).interrupt();
                    ConnectClient connectClient = new ConnectClient(port, elevator, connectTimeout);
                    connectors.put(elevator, connectClient);
                    connectClient.start();
                } else if (localElevator == elevator) {
                    //Log.i(this, "Establishment of connection to " + elevator + ": None (Local)."); //TODO: Comment in this and remove block underneath.
                    Log.i(this, "Establishment of connection to " + elevator + ": Client and Server (Local).");
                    if (connectors.get(elevator) != null) connectors.get(elevator).interrupt();
                    ConnectClient connectClient = new ConnectClient(port, elevator, connectTimeout);
                    connectors.put(elevator, connectClient);
                    connectClient.start();
                } else {
                    Log.i(this, "Establishment of connection to " + elevator + ": Server.");
                }
            }

            //Starting server:
            if(connectionServer != null) connectionServer.interrupt();
            connectionServer = new ConnectServer(port, LOCAL_ADDRESS);
            connectionServer.start();

        } catch (IOException e) {
            throw new NetworkException("Could not bind server to: " + LOCAL_ADDRESS + ":" + port);
        }
    }

    ElevatorConnection getConnection(InetAddress inetAddress){
        return connections.get(inetAddress);
    }

    /*******************************************
     * Public API
     *******************************************/
    public ElevatorConnection getConnection(Elevator elevator){
        return connections.get(elevator.getAddress());
    }
}
