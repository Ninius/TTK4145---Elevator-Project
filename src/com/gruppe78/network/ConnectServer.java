package com.gruppe78.network;

import com.gruppe78.network.ElevatorConnection;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
class ConnectServer extends Thread{
    private ServerSocket serverSocket;

    ConnectServer(int port, InetAddress localAddress) throws IOException {
        super();
        serverSocket = new ServerSocket(port, 20, localAddress);
    }

    @Override public void run(){
        Log.i(this, "Server successfully started.");
        try {
            while(!isInterrupted()){
                Socket clientSocket = serverSocket.accept();
                InetAddress clientAddress = clientSocket.getInetAddress();
                ElevatorConnection connection = Networker.get().getConnection(clientAddress);

                if(connection == null){
                    Log.i(this, "Socket for: "+clientAddress + " created. Accepted: NO. No matching ElevatorConnection. Closing the socket.");
                    clientSocket.close();
                }else{
                    Log.i(this, "Socket for: "+clientAddress + " created. Accepted: YES.");
                    connection.setConnectedSocket(clientSocket);
                }
            }
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Log.e(this, e);
        }
    }
}
