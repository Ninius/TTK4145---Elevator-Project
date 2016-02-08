package com.gruppe78.driver;

import javafx.application.Application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * The frontend interface to communicate with the simulator.
 */
public class SimulatorBridge {
    private static final int simulatorPortNumber = 9078;
    private static BufferedReader inFromSim;
    private static DataOutputStream outToSim;

    static boolean io_init(){
        try {
            Socket simulatorSocket = new Socket("localhost",simulatorPortNumber);
            inFromSim = new BufferedReader(new InputStreamReader(simulatorSocket.getInputStream()));
            outToSim = new DataOutputStream(simulatorSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private static void send(char id, int channel, int value){
        try {
            outToSim.writeBytes(id+";"+channel+";"+value+"\n");
        } catch (IOException e) {}
    }
    private static int read(){
        try {
            return Integer.valueOf(inFromSim.readLine());
        } catch (IOException e) {return -1;}
    }

    static void io_set_bit(int channel){
        send('S',channel,1);
    }
    static void io_clear_bit(int channel){
        send('C',channel,0);
    }
    static void io_write_analog(int channel, int value){
        send('W',channel,value);
    }
    static int io_read_bit(int channel){
        send('R',channel,-1);
        return read();
    }
    static int io_read_analog(int channel){
        send('A',channel,-1);
        return read();
    }
}
