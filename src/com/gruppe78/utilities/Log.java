package com.gruppe78.utilities;

/**
 * Created by student on 01.04.16.
 */
public class Log {
    public static void i(String className, String msg){
        print("INFO",className,msg);
    }
    public static void e(String className, String msg){
        print("ERROR",className,msg);
    }
    public static void e(String className, Exception ex){
        e(className,"");
        System.out.println(ex);
    }
    private static void print(String prefix, String className, String msg){
        System.out.println(prefix + " -- " + className + "("+Thread.currentThread().getName()+"): "+msg);
    }
}
