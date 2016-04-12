package com.gruppe78.network;

import com.gruppe78.model.Elevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by jespe on 12.04.2016.
 */
public class SocketReader extends Thread{
    private final Socket clientSocket;

    public SocketReader(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override public void run(){
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
