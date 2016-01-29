package com.gruppe78;

import java.sql.Driver;

/**
 * cc -G -I/java/include -I/java/include/solaris
 HelloWorld.c -o -fPIC libHelloWorld.so
 */
public class DriverHandler {
    public native void print();
    static{
        System.loadLibrary("DriverHandler");
    }


}
