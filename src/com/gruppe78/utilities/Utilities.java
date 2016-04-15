package com.gruppe78.utilities;

import com.gruppe78.model.Elevator;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class Utilities {
    private static final String NAME = Utilities.class.getSimpleName();

    public static void printNetworkInterfaces(){
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    Log.i(Utilities.class.getSimpleName(), "name: "+n.getName() +" : "+i.getHostAddress()+" : "+i.isAnyLocalAddress()+" "+i.isSiteLocalAddress()+" "+i.isLinkLocalAddress());
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
    }

    public static InetAddress getInetAddress(String IP){
        try {
            return InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            Log.i(NAME,"Invalid IP was given");
        }
        return null;
    }

    public static InetAddress getLocalIPV4Address(String prefix){
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) interfaces.nextElement();
                Enumeration inetAddresses = n.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress i = (InetAddress) inetAddresses.nextElement();
                    if(i instanceof Inet4Address && i.getHostAddress().split("\\.")[0].equals(prefix)) return i;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Elevator getConnectedElevator(ArrayList<Elevator> elevators){
        for(Elevator elevator : elevators){
            try {
                if(NetworkInterface.getByInetAddress(elevator.getAddress()) != null) return elevator;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
