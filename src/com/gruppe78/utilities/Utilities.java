package com.gruppe78.utilities;

import java.net.*;
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
    public static String getLocalAddress(){
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) interfaces.nextElement();
                Enumeration inetAddresses = n.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress i = (InetAddress) inetAddresses.nextElement();
                    if(i instanceof Inet4Address && i.getHostAddress().split("\\.")[0].equals("129")) return i.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration iFaces = NetworkInterface.getNetworkInterfaces(); iFaces.hasMoreElements();) {
                NetworkInterface iFace = (NetworkInterface) iFaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iFace.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    Log.i(Utilities.class.getSimpleName(), inetAddr.getHostAddress());
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}