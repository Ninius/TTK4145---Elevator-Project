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
    private static final String CHECK_MESSAGE = "group123";

    private final Elevator mElevator;
    private Socket mSocket;
    private BufferedReader mReader;
    private OutputStreamWriter mWriter;

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
                mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mWriter = new OutputStreamWriter(socket.getOutputStream());
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

    public synchronized boolean sendPrimitive(String message) {
        try {
            mWriter.write(message + "\n");
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

    private String readPrimitive(){
        try {
            return mReader.readLine(); //Blocking.
        } catch (IOException e) {
            Log.e(this, e);
            return null;
        }
    }

    private class ConnectionChecker extends LoopThread{
        @Override public void loopRun() {
            Log.d(this, "Sending connection message to "+mElevator);
            sendPrimitive(CHECK_MESSAGE);
        }

        @Override public int getInterval() {
            return CONNECTION_CHECK_INTERVAL;
        }
    }

    private class ConnectionReader extends LoopThread{
        @Override public void loopRun() {
            String message = readPrimitive();
            if(message == null || message.equals(CHECK_MESSAGE)) return;
            NetworkMessager.get().decodeMessage(message, mElevator);
        }

        @Override public int getInterval() {
            return 0;
        }
    }
}
