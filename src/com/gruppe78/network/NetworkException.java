package com.gruppe78.network;

/**
 * Created by jespe on 15.04.2016.
 */
public class NetworkException extends Exception {
    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
