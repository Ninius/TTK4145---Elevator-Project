package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;

public class ConnectedManager {/*
    private static final String NAME = ConnectedManager.class.getSimpleName();
    private static final byte[] message = "Hello I'm Alive".getBytes();
    private static final int PORT = 6300;
    private static final int SEND_INTERVAL = 300;
    private static final int RECEIVE_INTERVAL = 100;
    private static final int MAX_MESSAGE_SKIPS = 3;

    /*************************************
     * Singleton
     ************************************//*
    private static ConnectedManager sConnectedManager;
    private ConnectedManager(){}
    public static ConnectedManager get(){
        if(sConnectedManager == null){
            sConnectedManager = new ConnectedManager();
        }
        return sConnectedManager;
    }

    private Thread sender;
    private Thread receiver;
    private Thread connectedChecker;

    public void start(){
        sender = new SendingThread();
        sender.setName(SendingThread.class.getSimpleName());
        sender.start();

        receiver = new ReceivingThread();
        receiver.setName(ReceivingThread.class.getSimpleName());
        receiver.start();

        connectedChecker = new ConnectedChecker();
        connectedChecker.setName(ConnectedChecker.class.getSimpleName());
        connectedChecker.start();
    }

    private class SendingThread extends Thread {
        @Override public void run(){
            DatagramSocket sendingSocket = null;
            try {
                sendingSocket = new DatagramSocket();
                while(true){
                    for(Elevator elevator: SystemData.get().getElevatorList()){
                        if(elevator.isLocal()) continue;
                        DatagramPacket packet = new DatagramPacket(message, message.length, elevator.getAddress(), PORT);
                        sendingSocket.send(packet);
                    }
                    Thread.sleep(SEND_INTERVAL);
                }
            } catch (InterruptedException | IOException e) {
                if(sendingSocket != null) sendingSocket.close();
                e.printStackTrace();
            }
        }
    }

    private class ReceivingThread extends Thread{
        @Override public void run(){
            DatagramSocket receivingSocket = null;
            try{
                receivingSocket = new DatagramSocket(PORT);
                while(true){
                    DatagramPacket receivePacket = new DatagramPacket(message, message.length);
                    receivingSocket.receive(receivePacket);

                    String message = new String(receivePacket.getData());
                    String IP = receivePacket.getAddress().getHostName();
                    Log.i(NAME,"Received message from "+IP+": "+message);

                    if(!receivePacket.getData().equals(message)){
                        Log.i(NAME, "Message from "+IP+" did not match.");
                        continue;
                    }

                    Elevator receivingElevator = SystemData.get().getElevator(receivePacket.getAddress());
                    if(receivingElevator == null){
                        Log.i(NAME, "Elevator IP-address "+IP+" was not in the system.");
                        continue;
                    }
                    receivingElevator.setConnected(true);
                    Thread.sleep(RECEIVE_INTERVAL);
                }
            } catch (IOException | InterruptedException e) {
                if(receivingSocket != null) receivingSocket.close();
                e.printStackTrace();
            }
        }
    }

    private class ConnectedChecker extends Thread{
        @Override public void run(){
            try{
                while(true) {
                    for (Elevator elevator : SystemData.get().getElevatorList()) {
                        if (elevator.isLocal()) continue;
                        if (System.currentTimeMillis() > elevator.getLastConnectTime() + SEND_INTERVAL * MAX_MESSAGE_SKIPS) {
                            elevator.setConnected(false);
                        }
                    }
                    Thread.sleep(SEND_INTERVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
