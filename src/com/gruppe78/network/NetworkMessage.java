package com.gruppe78.network;

import com.gruppe78.model.Elevator;

import java.io.Serializable;

/**
 * Created by jespe on 17.04.2016.
 */
public class NetworkMessage implements Serializable {
    private String message;
    private Object data;

    public NetworkMessage(String message, Object data){
        this.message = message;
        this.data = data;
    }
    public String getMessage(){
        return message;
    }
    public Object getData(){
        return data;
    }

    @Override
    public String toString(){
        return "NetMsg("+message+": data:" +data+")";
    }
}
