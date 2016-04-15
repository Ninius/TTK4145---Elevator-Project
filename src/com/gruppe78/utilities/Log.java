package com.gruppe78.utilities;

/**
 * Created by student on 01.04.16.
 */
public class Log {
    public static void d(Object caller, String msg){Log.d(caller.getClass().getSimpleName(),msg);}
    public static void i(Object caller, String msg){Log.i(caller.getClass().getSimpleName(),msg);}
    public static void e(Object caller, String msg){Log.e(caller.getClass().getSimpleName(),msg);}
    public static void e(Object caller, Exception e){Log.e(caller.getClass().getSimpleName(),e);}

    public static void d(String className, String msg) { println("DEBUG",className,msg);}
    public static void i(String className, String msg){
        println("INFO",className,msg);
    }
    public static void e(String className, String msg){
        println("ERROR",className,msg);
    }
    public static void e(String className, Exception ex){
        print("ERROR",className,"");
        System.out.println(ex);
    }
    private static void println(String prefix, String className, String msg){
        print(prefix,className,msg);
        System.out.println();
    }
    private static void print(String prefix, String className, String msg){
        System.out.print(prefix + " -- " + className + "("+Thread.currentThread().getName()+") -- "+msg);
    }
}
