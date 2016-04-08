package com.gruppe78.utilities;

/**
 * Created by student on 01.04.16.
 */
public class Log {
    public static void i(String className, String msg){
        System.out.println("INFO -- " + className + "("+Thread.currentThread().getName()+"): "+msg);
    }
    public static void e(String className, Exception ex){
        System.out.println("ERROR -- " + className + "("+Thread.currentThread().getName()+"):");
        System.out.println(ex);
    }
}
