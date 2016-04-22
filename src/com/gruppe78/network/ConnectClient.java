package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;

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
        Exception lastException = null;
        while (!isInterrupted()){
            try {
                socket.connect(mConnectPoint, mSocketConnectTimeout);
                Log.i(this, "Socket for: "+mElevator+" created. Thread stopping.");
                NetworkStarter.get().getConnection(mElevator).setConnectedSocket(socket);
                return;
            } catch (SocketTimeoutException e) {
                socket = new Socket();
                if(lastException instanceof SocketTimeoutException) continue;
                lastException = e;
                Log.i(this, "Socket Timed out for "+mElevator+ ". Elevator is reachable, but is not responding. Trying again.");
            } catch (NoRouteToHostException e){
                socket = new Socket();
                if(lastException instanceof NoRouteToHostException) continue;
                lastException = e;
                Log.i(this, "NoRouteToHostException for "+mElevator+". Probably due to connection loss to router. Trying again.");
            } catch (ConnectException e) {
                socket = new Socket();
                if(lastException instanceof ConnectException) continue;
                lastException = e;
                Log.i(this, "ConnectException for "+mElevator+". Reaching, but no server responding. Trying again.");
            } catch (IOException e){
                Log.e(this, "Unknown IOException for "+mElevator+". Stopping ConnectClient...");
                Log.e(this, e);
                return;
            }
        }
    }
}
