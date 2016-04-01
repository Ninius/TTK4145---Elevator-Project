package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;

/**
 * Set up server after discovery.
 */
public class DeviceSearcher {
    private static DeviceSearcher sDeviceSearcher;
    private static final String REQUEST_MSG = "REQUEST_GROUP123";
    private static final String RESPONSE_MSG = "RESPONSE_GROUP123";
    private static int discoveryPort;

    private DatagramSocket socket;

    private DeviceSearcher(int discoveryPort){
        this.discoveryPort = discoveryPort;
    }

    public static void get(int discoveryPort){
        if(sDeviceSearcher == null){
            sDeviceSearcher = new DeviceSearcher(discoveryPort);
        }
        sDeviceSearcher = new DeviceSearcher(discoveryPort);
    }

    public void searchForElevators(int discoveryPort){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Find the server using UDP broadcast
                try {
                    //Open a random port to send the package
                    DatagramSocket c = new DatagramSocket();
                    c.setBroadcast(true);

                    byte[] sendData = REQUEST_MSG.getBytes();

                    //Try the 255.255.255.255 first
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), discoveryPort);
                        c.send(sendPacket);
                        Log.i(getClass().getName(), "Request packet sent to: 255.255.255.255 (DEFAULT)");
                    } catch (Exception e) {
                    }

                    //Wait for a response
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    c.receive(receivePacket);

                    //We have a response
                    Log.i(getClass().getName(), "Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                    //Check if the message is correct
                    String message = new String(receivePacket.getData()).trim();
                    if (message.equals(RESPONSE_MSG)) {
                        Elevator elevator = new Elevator(false);
                    }
                    c.close();
                } catch (IOException ex) {
                    Log.e(getClass().getName(),ex);
                }
            }
        });
        thread.setName("Discover Client Thread");
        thread.start();
    }

    public void startElevatorSearchServer(int discoveryPort){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
                    socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);

                    while (true) {
                        Log.i(getClass().getName(), "Ready to receive discover messages.");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        socket.receive(packet);

                        //Packet received
                        Log.i(getClass().getName(), "Discovery packet received from: " + packet.getAddress().getHostAddress());
                        Log.i(getClass().getName(), "Packet received; data: " + new String(packet.getData()));

                        //See if the packet holds the right command (message)
                        String message = new String(packet.getData()).trim();
                        if (message.equals(REQUEST_MSG)) {
                            byte[] sendData = RESPONSE_MSG.getBytes();

                            //Send a response
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            socket.send(sendPacket);

                            Log.i(getClass().getName(), "Sent packet to: " + sendPacket.getAddress().getHostAddress());
                        }
                    }
                } catch (IOException ex) {
                    Log.e(getClass().getName(), ex);
                }
            }
        });
        thread.setName("Discover Server Thread");
        thread.start();
    }
}
