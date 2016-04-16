package com.gruppe78.network;

import com.gruppe78.model.Elevator;
import com.gruppe78.utilities.Log;

/**
 * Created by jespe on 14.04.2016.
 */
public class NetworkMessager {
    private static NetworkMessager sNetworkMessager = new NetworkMessager();

    private NetworkMessager(){

    }
    static NetworkMessager get(){
        return sNetworkMessager;
    }

    void decodeMessage(String message, Elevator sender){
        Log.i(this, "Message received from "+sender+":"+message);

}
        }
