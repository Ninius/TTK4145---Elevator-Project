package com.gruppe78.utilities;

/**
 * Created by student on 01.04.16.
 */
public class Log {
    public static void i(String name, String msg){
        System.out.println("INFO -- "+name + "("+Thread.currentThread().getName()+"): "+msg);
    }
    public static void e(String name, Exception ex){
        System.out.println("ERROR -- "+name + "("+Thread.currentThread().getName()+"):");
        System.out.println(ex);
    }
}
