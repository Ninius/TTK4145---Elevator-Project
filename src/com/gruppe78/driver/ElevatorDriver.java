package com.gruppe78.driver;

import com.gruppe78.Main;

import java.io.File;

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
class ElevatorDriver implements Driver {
    private static ElevatorDriver sDriver;
    private static final String LIBRARY_NAME = "libElevatorDriver.so";

    private ElevatorDriver(){
        File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String libraryPath = new File(jarPath.getParent(), LIBRARY_NAME).getAbsolutePath();
        try{
            System.load(libraryPath);
            System.out.println("Loaded driver library at "+libraryPath);
        }catch (UnsatisfiedLinkError error){
            System.out.println("Could not locate driver library at: "+libraryPath);
        }
    }

    static Driver get() {
        if(sDriver == null){
            sDriver = new ElevatorDriver();
        }
        return sDriver;
    }

    //Native methods:
    public native boolean io_init(); //Returns true if successful.
    public native void io_set_bit(int channel);
    public native void io_clear_bit(int channel);
    public native void io_write_analog(int channel, int value);
    public native int io_read_bit(int channel);
    public native int io_read_analog(int channel);
}
