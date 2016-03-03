package com.gruppe78.driver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * The frontend interface to communicate with the simulator.
 * TODO: Possibly make an own thread so delay is not present.
 */
public class SimulatorDriver implements Driver{
    private static SimulatorDriver sDriver;
    private static DatagramSocket simulatorSocket;
    private static InetAddress simulatorAddress;
    private static final int RECEIVE_PACKET_SIZE = 1024;
    private static final int RECEIVE_TIMEOUT = 50;
    private static final int simulatorPortNumber = 9078;

    private SimulatorDriver(){}

    static Driver get() {
        if(sDriver == null){
            sDriver = new SimulatorDriver();
        }
        return sDriver;
    }

    /**************************************
     * Private helpers
     *************************************/

    private static void send(char id, int channel, int value){
        String message = id+";"+channel+";"+value+";";
        byte[] sendData = message.getBytes();
        DatagramPacket packet = new DatagramPacket(sendData,sendData.length,simulatorAddress,simulatorPortNumber);
        try {
            simulatorSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static int read(){
        byte[] receiveData = new byte[RECEIVE_PACKET_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, RECEIVE_PACKET_SIZE);
        try {
            simulatorSocket.receive(receivePacket);
            String message = new String(receivePacket.getData()).split(";")[0];
            return Integer.valueOf(message);
        } catch (SocketTimeoutException e) {
            System.out.println("Receive timed out");
            return -Integer.MAX_VALUE;
        } catch (IOException e) {
            e.printStackTrace();
            return -Integer.MAX_VALUE;
        }
    }

    /************************************
     * Driver Interface implementation
     ************************************/

    public boolean io_init(){
        try {
            simulatorSocket = new DatagramSocket();
            simulatorSocket.setSoTimeout(RECEIVE_TIMEOUT);
            simulatorAddress = InetAddress.getByName("localhost");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void io_set_bit(int channel){
        send('S',channel,1);
    }
    public void io_clear_bit(int channel){
        send('C',channel,0);
    }
    public void io_write_analog(int channel, int value){
        send('W',channel,value);
    }
    public int io_read_bit(int channel){
        send('R',channel,-1);
        return read();
    }
    public int io_read_analog(int channel){
        send('A',channel,-1);
        return read();
    }
}
