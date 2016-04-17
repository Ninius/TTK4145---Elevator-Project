package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.LoopThread;

import java.io.*;
import java.net.Socket;

/**
 * Representing an connection from this elevator/machine to an other elevator/machine.
 */
public class ElevatorConnection {
    private static final int CONNECTION_CHECK_INTERVAL = 5000;
    private static final NetworkMessage CHECK_MESSAGE = new NetworkMessage("check","check");

    private final Elevator mElevator;
    private Socket mSocket;
    private ObjectInputStream mReader;
    private ObjectOutputStream mWriter;

    private ConnectionChecker mConnectionChecker = new ConnectionChecker();
    private ConnectionReader mMessageReader = new ConnectionReader();

    ElevatorConnection(Elevator elevator){
        mElevator = elevator;
    }

    private boolean isValid(Socket socket){
        return mSocket != null && (mSocket.isClosed() || !mSocket.isConnected());
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
    private void setConnectionStatus(boolean status){
        mElevator.setConnected(status);
    }

    public synchronized boolean sendMessage(NetworkMessage message) {
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
            LocalElevatorBroadcaster.get().decodeMessage(message, mElevator);
        }

        @Override public int getInterval() {
            return 0;
        }
    }
}
