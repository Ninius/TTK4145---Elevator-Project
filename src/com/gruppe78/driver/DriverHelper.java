package com.gruppe78.driver;

import com.gruppe78.model.Button;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Direction;

/**
 * DriverHelper works as a bridge between the driver library written in C and the rest of the application.
 *
 * GUIDE TO CREATING JNI:
 * 1) Make h-header.
 * 2) Write c-file:
 * 3) Compile c-file as library: cc -G -I/java/include -I/java/include/linux name.c -o -fPIC libHelloWorld.so
 */

public class DriverHelper {
    private static Driver sDriver;
    public static final int ELEVATOR_DRIVER = 1;
    public static final int SIMULATOR_DRIVER = 2;
    private static final int MOTOR_SPEED = 2800;

    /***************************************************
     * PUBLIC API
     ****************************************************/

    public synchronized static boolean init(int type) {
        sDriver = (type == 1) ? ElevatorDriver.get() : SimulatorDriver.get();
        if(!sDriver.io_init()){
            return false;
        }
        for (Floor floor : Floor.values()) {
            for(Button b : Button.values()){
                setButtonLamp(b, floor, false);
            }
        }
        setMotorDirection(Direction.NONE);
        setStopLamp(false);
        setDoorOpenLamp(false);
        setFloorIndicator(Floor.FLOOR0);
        return true;
    }

    public synchronized static boolean isButtonPressed(Button button, Floor floor) {
        return sDriver.io_read_bit(DriverChannels.BUTTON_CHANNEL_MATRIX[floor.index][button.index]) == 1;
    }

    public synchronized static Floor getElevatorFloor() {
        if (sDriver.io_read_bit(DriverChannels.SENSOR_FLOOR1) == 1) {
            return Floor.FLOOR0;
        } else if (sDriver.io_read_bit(DriverChannels.SENSOR_FLOOR2) == 1) {
            return Floor.FLOOR1;
        } else if (sDriver.io_read_bit(DriverChannels.SENSOR_FLOOR3) == 1) {
            return Floor.FLOOR2;
        } else if (sDriver.io_read_bit(DriverChannels.SENSOR_FLOOR4) == 1) {
            return Floor.FLOOR3;
        } else {
            return null;
        }
    }

    public synchronized static boolean isStopPressed() {
        return sDriver.io_read_bit(DriverChannels.STOP) == 1;
    }

    public synchronized static boolean isObstructionPressed() {
        return sDriver.io_read_bit(DriverChannels.OBSTRUCTION) == 1;
    }

    /****************************************************
     * PACKAGE-PRIVATE API - Changes to driver not allowed outside package.
     ****************************************************/

    static synchronized void setMotorDirection(Direction direction) {
        switch (direction){
            case NONE:
                sDriver.io_write_analog(DriverChannels.MOTOR, 0);
                return;
            case DOWN:
                sDriver.io_set_bit(DriverChannels.MOTORDIR);
                sDriver.io_write_analog(DriverChannels.MOTOR, MOTOR_SPEED);
                return;
            case UP:
                sDriver.io_clear_bit(DriverChannels.MOTORDIR);
                sDriver.io_write_analog(DriverChannels.MOTOR,MOTOR_SPEED);
        }
    }


    static synchronized void setButtonLamp(Button button, Floor floor, boolean turnOn) {
        if (turnOn) {
            sDriver.io_set_bit(DriverChannels.LAMP_CHANNEL_MATRIX[floor.index][button.index]);
        } else {
            sDriver.io_clear_bit(DriverChannels.LAMP_CHANNEL_MATRIX[floor.index][button.index]);
        }
    }


    static synchronized void setFloorIndicator(Floor floor) {
        if(floor == null) return;

        // Binary encoding. One light must always be on.
        if ((floor.index & 0x02) == 2) {
            sDriver.io_set_bit(DriverChannels.LIGHT_FLOOR_IND1);
        } else {
            sDriver.io_clear_bit(DriverChannels.LIGHT_FLOOR_IND1);
        }

        if ((floor.index & 0x01) == 1) {
            sDriver.io_set_bit(DriverChannels.LIGHT_FLOOR_IND2);
        } else {
            sDriver.io_clear_bit(DriverChannels.LIGHT_FLOOR_IND2);
        }
    }


    static synchronized void setDoorOpenLamp(boolean turnOn) {
        if (turnOn) {
            sDriver.io_set_bit(DriverChannels.LIGHT_DOOR_OPEN);
        } else {
            sDriver.io_clear_bit(DriverChannels.LIGHT_DOOR_OPEN);
        }
    }


    static synchronized void setStopLamp(boolean turnOn) {
        if (turnOn) {
            sDriver.io_set_bit(DriverChannels.LIGHT_STOP);
        } else {
            sDriver.io_clear_bit(DriverChannels.LIGHT_STOP);
        }
    }

}