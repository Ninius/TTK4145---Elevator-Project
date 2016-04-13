package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Networker {
    private static final String NAME = Networker.class.getSimpleName();
    private static final int SERVER_PORT = 4300;

    private static Networker sNetworker;

    private HashMap<Elevator, ElevatorConnection> connections = new HashMap<>();
    private WelcomeThread welcomeThread;

    /*****************************************
     * Initialization
     *****************************************/

    private Networker(){
        for(Elevator elevator : SystemData.get().getElevatorList()){
            //connections.put(elevator, new ElevatorConnection(elevator.getInetAddress()));
        }
    }
    public static Networker get(){
        if(sNetworker == null){
            sNetworker = new Networker();
        }
        return sNetworker;
    }

    /*******************************************
     * Public API
     *******************************************/
    public void startAcceptingConnections(){
        welcomeThread = new WelcomeThread();
        welcomeThread.setName(WelcomeThread.class.getSimpleName());
        welcomeThread.start();
    }
    public void startConnectingToElevators(){
        Elevator localElevator = SystemData.get().getLocalElevator();
        for(Elevator elevator : SystemData.get().getElevatorList()){
            if(elevator == localElevator) continue;
            if(elevator.hasHigherIDThan(localElevator)){
                Log.i(NAME, localElevator + " connecting to " + elevator);
                connectTo(elevator);
            }
        }
    }

    private void connectTo(Elevator elevator){
        SystemData data = SystemData.get();
        Socket clientSocket = new Socket();
        try {
            clientSocket.connect(new InetSocketAddress(elevator.getInetAddress(), SERVER_PORT), 0);
            Log.i(NAME, "Socket connecting to "+elevator+" created.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class WelcomeThread extends Thread{
        private ServerSocket serverSocket;

        @Override public void run(){
            Socket clientSocket = null;
            try{
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true){
                try {
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread clientThread = new SocketReader(clientSocket);
                clientThread.setName(SocketReader.class.getSimpleName());
                clientThread.start();
            }
        }
    }
}
