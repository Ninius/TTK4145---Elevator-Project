package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Created by jespe on 15.04.2016.
 */
class ConnectClient extends Thread{
    private Elevator mElevator;
    private SocketAddress mConnectPoint;
    private int mSocketConnectTimeout;

    ConnectClient(int port, Elevator elevator, int socketConnectTimeout){
        super();
        mElevator = elevator;
        mConnectPoint = new InetSocketAddress(mElevator.getAddress(), port);
        mSocketConnectTimeout = socketConnectTimeout;
    }

    @Override public void run(){
        Socket socket = new Socket();
        Log.i(this, "Trying to connect to " + mElevator);
        while (!isInterrupted()){
            try {
                socket.connect(mConnectPoint, mSocketConnectTimeout);
                Log.i(this, "Socket for: "+mElevator+" created. Thread stopping.");
                Networker.get().getConnection(mElevator).setConnectedSocket(socket);
                return;
            } catch (SocketTimeoutException timeOutException){
                socket = new Socket();
            } catch (IOException e) {
                Log.e(this, e);
                return;
            }
        }
    }
}
