package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Networker {
    private static Networker sNetworker;

    private static final int SERVER_PORT = 4300;
    private WelcomeThread welcomeThread;

    /*****************************************
     * Initialization
     *****************************************/

    private Networker(){

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

    private void connectTo(Elevator elevator){
        SystemData data = SystemData.get();
        data.getLocalElevator().getInetAddress().getAddress();
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
