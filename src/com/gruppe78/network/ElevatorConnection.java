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
    private static final int MESSAGE_READ_INTERVAL = 0;
    private static final String CHECK_MESSAGE = "group123";

    private final Elevator mElevator;
    private Socket mSocket;
    private BufferedReader mReader;
    private OutputStreamWriter mWriter;

    private final Object writeLock = new Object();
    private final Object readLock = new Object();

    private boolean mConnection = false;

    private ConnectionChecker mConnectionChecker = new ConnectionChecker();
    private ConnectionReader mMessageReader = new ConnectionReader();

    ElevatorConnection(Elevator elevator){
        mElevator = elevator;
    }

    synchronized void setConnectedSocket(Socket socket){
        if(socket == null || mSocket != null) return;
        Log.d(this, "setConnectedSocket called");
        synchronized (writeLock){
            synchronized (readLock){
                try {
                    mSocket = socket;
                    mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    mWriter = new OutputStreamWriter(socket.getOutputStream());
                    Log.i(this, "Socket successfully set up to "+mElevator);
                    mConnectionChecker.start();
                    mMessageReader.start();
                } catch (IOException e) {
                    setConnection(false);
                    e.printStackTrace();
                }
            }
        }
    }
    private void setConnection(boolean connection){
        mConnection = connection;
    }

    public boolean sendPrimitive(String message) {
        Log.d(this, "Send primitive called.");
        synchronized (writeLock){
            try {
                mWriter.write(message + "\n");
                mWriter.flush();
                setConnection(true);
                return true;
            } catch (IOException e) {
                Log.i(this, "Could not send msg. Setting connection status to false.");
                setConnection(false);
                e.printStackTrace();
                return false;
            }
        }
    }
    public String readPrimitive(){
        Log.d(this, "Read primitive called.");
        synchronized (readLock){
            try {
                Log.d(this, "Reading message..");
                String message = mReader.readLine();
                //setConnection(true);
                return message;
            } catch (IOException e) {
                Log.i(this, "Could not read msg. Setting connection status to false.");
                setConnection(false);
                e.printStackTrace();
                return null;
            }
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
            Log.d(this, "Reading message from "+ mElevator);
            String message = readPrimitive();
            if(message == null) return;
            NetworkMessager.get().decodeMessage(message, mElevator);
        }

        @Override public int getInterval() {
            return MESSAGE_READ_INTERVAL;
        }
    }
}
