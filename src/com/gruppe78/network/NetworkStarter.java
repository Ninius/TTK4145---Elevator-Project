package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;


public class NetworkStarter {
    private final InetAddress LOCAL_ADDRESS;
    private SystemData data;

    private static NetworkStarter sNetworkStarter;

    private HashMap<InetAddress, ElevatorConnection> connections = new HashMap<>();
    private ConnectServer connectionServer;
    private HashMap<Elevator, ConnectClient> connectors = new HashMap<>();

    /*****************************************
     * Initialization
     *****************************************/

    private NetworkStarter(){
        data = SystemData.get();
        LOCAL_ADDRESS = data.getLocalElevator().getAddress();
        Elevator localElevator = data.getLocalElevator();
        for(Elevator elevator : data.getElevatorList()){
            if (localElevator.getID() < elevator.getID()) {
                Log.i(this, "Establishment of connection to " + elevator + ": Client.");
                connections.put(elevator.getAddress(), new ElevatorConnection(elevator, true));
            } else if (localElevator.getID() == elevator.getID()) {
                Log.i(this, "Establishment of connection to " + elevator + ": None (Local).");
            } else {
                Log.i(this, "Establishment of connection to " + elevator + ": Server.");
                connections.put(elevator.getAddress(), new ElevatorConnection(elevator, false));
            }
        }
    }
    public static NetworkStarter get(){
        if(sNetworkStarter == null){
            sNetworkStarter = new NetworkStarter();
        }
        return sNetworkStarter;
    }

    public void createElevatorConnections(int connectTimeout, int port) throws NetworkException {
        try {
            //Starting clients:
            Elevator localElevator = data.getLocalElevator();
            for(Elevator elevator : data.getElevatorList()) {
                if (localElevator.getID() >= elevator.getID()) continue;

                ConnectClient connectClient = new ConnectClient(port, elevator, connectTimeout);

                if (connectors.get(elevator) != null) connectors.get(elevator).interrupt();
                connectors.put(elevator, connectClient);

                connectClient.start();
            }

            //Starting server:
            if(connectionServer != null) connectionServer.interrupt();
            connectionServer = new ConnectServer(port, LOCAL_ADDRESS);
            connectionServer.start();

        } catch (IOException e) {
            throw new NetworkException("Could not bind server to: " + LOCAL_ADDRESS + ":" + port);
        }
    }

    public ElevatorConnection getConnection(Elevator elevator){
        return connections.get(elevator.getAddress());
    }
    ElevatorConnection getConnection(InetAddress inetAddress){
        return connections.get(inetAddress);
    }



}
