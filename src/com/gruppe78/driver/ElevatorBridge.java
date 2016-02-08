package com.gruppe78.driver;

/**
 * Created by student on 05.02.16.
 *
 *
 *
 * Compile library:
 * cc -I/home/student/Gruppe123/jdk1.8.0_71/include -I/home/student/Gruppe123/jdk1.8.0_71/include/linux -name.c -o nameLib.so -shared -fPIC -lcomedi -lm
 *
 *
 */
public class ElevatorBridge {
    private static final String LIBRARY_PATH = "/home/student/Gruppe123/IntellijProjects/src/libDriverBridge.so";
    static{
        try{
            System.load(LIBRARY_PATH);
        }catch (UnsatisfiedLinkError error){
            System.out.println("Could not locate driver library at: "+LIBRARY_PATH);
        }
    }

    //Native methods:
    static native boolean io_init(); //Returns true if successful.
    static native void io_set_bit(int channel);
    static native void io_clear_bit(int channel);
    static native void io_write_analog(int channel, int value);
    static native int io_read_bit(int channel);
    static native int io_read_analog(int channel);
}
