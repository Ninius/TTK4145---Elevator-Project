package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * Set up server after discovery.
 */
public class DeviceSearcher {
    private static final String NAME = DeviceSearcher.class.getSimpleName();
    private static final String REQUEST_MSG = "REQUEST_GROUP123";
    private static final String RESPONSE_MSG = "RESPONSE_GROUP123";
    private static final int DISCOVERY_PORT = 4123;
    private static final int DISCOVER_TIMEOUT = 5000;
    private static final String BROADCAST_ADDRESS = "129.241.187.255";

    private static DeviceSearcher sDeviceSearcher;

    private DatagramSocket socket;

    private DeviceSearcher(){
    }

    public static DeviceSearcher get(){
        if(sDeviceSearcher == null){
            sDeviceSearcher = new DeviceSearcher();
        }
        return sDeviceSearcher;
    }

    public void searchForElevators(){
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);
            c.setSoTimeout(DISCOVER_TIMEOUT);

            byte[] sendData = REQUEST_MSG.getBytes();

            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(BROADCAST_ADDRESS), DISCOVERY_PORT);
                c.send(sendPacket);
                Log.i(NAME, "Request packet sent to: "+BROADCAST_ADDRESS + " - Waiting for reply");
            } catch (Exception e) {
                Log.e(NAME, e);
            }

            //Wait for a response
            byte[] receiveBuffer = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            c.receive(receivePacket);

            //We have a response
            Log.i(NAME, "Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals(RESPONSE_MSG)) {
                Log.i(NAME, "Response match!");
            }
            c.close();
        }catch (SocketTimeoutException e){
            Log.i(NAME,"Timeout on receive");
        } catch (IOException ex) {
            Log.e(getClass().getName(),ex);
        }
    }

    public void startElevatorSearchServer(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Keep a socket open to listen to all the UDP traffic that is destined for this port
                    socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"));
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
