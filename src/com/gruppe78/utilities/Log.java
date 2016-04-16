package com.gruppe78.utilities;

/**
 * Created by student on 01.04.16.
 */
public class Log {
    public static void d(Object caller, String msg){Log.d(caller.getClass().getSimpleName(),msg);}
    public static void i(Object caller, String msg){Log.i(caller.getClass().getSimpleName(),msg);}
    public static void e(Object caller, String msg){Log.e(caller.getClass().getSimpleName(),msg);}
    public static void e(Object caller, Exception e){Log.e(caller.getClass().getSimpleName(),e);}

    public static void d(String className, String msg) { print("DEBUG",className,msg,true);}
    public static void i(String className, String msg){
        print("INFO",className,msg,true);
    }
    public static void e(String className, String msg){
        print("ERROR",className,msg,true);
    }
    public static void e(String className, Exception ex){
        print("ERROR",className,"", false);
        System.out.println(ex);
    }
    public static void s(String className, Exception ex){
        print("SYSEXIT",className,"",false);
        System.out.println(ex);
    }

    private static String lastPrefix = "";
    private static String lastClassThreadName = "";

    private static StringBuilder builder = new StringBuilder();

    private static void print(String prefix, String className, String msg, boolean newline){
        prefix = String.format("%-7s", prefix);
        String classNameThread = String.format("%25s", className + "("+Thread.currentThread().getName()+")");
        System.out.print(prefix + " - " + classNameThread+" - "+msg + (newline ? "\n" : ""));
    }
}
