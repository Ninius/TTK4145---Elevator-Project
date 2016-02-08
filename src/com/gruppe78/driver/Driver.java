package com.gruppe78.driver;

import javafx.application.Application;

/**
 * A class that chooses over Simulator and the ElevatorDriver
 */
public class Driver {
    private static final boolean useSimulator = true;
    //driver methods:
    //Returns true if successful.
    static boolean io_init(){
        if(useSimulator){
            return SimulatorBridge.io_init();
        }else{
            return ElevatorBridge.io_init();
        }
    }

    static void io_set_bit(int channel){
        if(useSimulator){
            SimulatorBridge.io_set_bit(channel);
        }else{
            ElevatorBridge.io_set_bit(channel);
        }
    }

    static void io_clear_bit(int channel){
        if(useSimulator){
            SimulatorBridge.io_clear_bit(channel);
        }else{
            ElevatorBridge.io_clear_bit(channel);
        }
    }

    static void io_write_analog(int channel, int value){
        if(useSimulator){
            SimulatorBridge.io_write_analog(channel,value);
        }else{
            ElevatorBridge.io_write_analog(channel,value);
        }
    }

    static int io_read_bit(int channel){
        return useSimulator ? SimulatorBridge.io_read_bit(channel) : ElevatorBridge.io_read_bit(channel);
    }

    static int io_read_analog(int channel){
        return useSimulator ? SimulatorBridge.io_read_analog(channel) : ElevatorBridge.io_read_analog(channel);
    }
}
