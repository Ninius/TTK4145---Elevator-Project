package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.SystemData;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.LoopThread;
import com.gruppe78.utilities.Utilities;

import java.io.*;
import java.net.Socket;

/**
 * Representing an connection from this elevator/machine to an other elevator/machine.
 */
public class ElevatorConnection {
    private static final int CONNECTION_CHECK_INTERVAL = 5000;
    private static final NetworkMessage CHECK_MESSAGE = new NetworkMessage("check","check");

    private final Elevator mElevator;
    private final Elevator mLocalElevator;
    private boolean mClient;
    private Socket mSocket;
    private ObjectInputStream mReader;
    private ObjectOutputStream mWriter;

    private ConnectionChecker mConnectionChecker = new ConnectionChecker();
    private ConnectionReader mMessageReader = new ConnectionReader();

    ElevatorConnection(Elevator elevator, boolean client){
        mElevator = elevator;
        mLocalElevator = SystemData.get().getLocalElevator();
        mClient = client;
    }

    private boolean isValid(Socket socket){
        return socket != null && (socket.isClosed() || !socket.isConnected());
    }

    synchronized void setConnectedSocket(Socket socket){
        if(isValid(socket)){
            Log.e(this, mElevator + ":Tried to set socket. Socket was not valid!");
        }else if(isValid(mSocket)){
            Log.e(this, mElevator + ":Tried to set socket. Valid socket already set!");
        }else{
            try {
                mSocket = socket;
                mReader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                mWriter = new ObjectOutputStream(socket.getOutputStream());
                Log.i(this, mElevator + ":Socket successfully set up to.");
                mConnectionChecker.start();
                mMessageReader.start();
            } catch (IOException e) {
                setConnectionStatus(false);
                e.printStackTrace();
            }
        }
    }

    //Only set false if
    private void setConnectionStatus(boolean connected){
        if(!connected){
            if(Utilities.isElevatorLocalConnected(mLocalElevator)){
                mElevator.setConnected(connected);
            }else{
                mLocalElevator.setConnected(connected);
            }
        }else{
            mElevator.setConnected(true);
            mLocalElevator.setConnected(true);
        }
    }

    public synchronized boolean sendMessage(NetworkMessage message) {
        if(mSocket == null) return false;
        try {
            mWriter.writeObject(message);
            mWriter.flush();
            setConnectionStatus(true);
            return true;
        } catch (IOException e) { //Todo, check different exception types. Test.
            setConnectionStatus(false);
            Log.i(this, "Could not send msg. Setting connection status to false.");
            Log.e(this, e);
            return false;
        }
    }

    private NetworkMessage readMessage(){
        try {
            return (NetworkMessage) mReader.readObject(); //Blocking.
        } catch (IOException e) {
            Log.e(this, e);
            return null;
        } catch (ClassNotFoundException e) {
            Log.e(this, e);
            return null;
        }
    }

    private class ConnectionChecker extends LoopThread{
        @Override public void loopRun() {
            Log.d(this, "Sending connection message to "+mElevator);
            sendMessage(CHECK_MESSAGE);
        }

        @Override public int getInterval() {
            return CONNECTION_CHECK_INTERVAL;
        }
    }

    private class ConnectionReader extends LoopThread{
        @Override public void loopRun() {
            NetworkMessage message = readMessage();
            if(message == null || message.equals(CHECK_MESSAGE)) return;
            NetworkMessenger.get().decodeMessage(message, mElevator);
        }

        @Override public int getInterval() {
            return 0;
        }
    }
}
