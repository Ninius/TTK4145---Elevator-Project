package com.gruppe78;

/**
 * DriverHandler works as a bridge between the driver library written in C and the rest of the application.
 * It offers enum types and booleans to ensure that the rest of the application works within bounds of the elevator.
 *
 * GUIDE TO CREATING JNI:
 * 1) Make h-header.
 * 2) Write c-file:
 * 3) Compile c-file as library: cc -G -I/java/include -I/java/include/linux name.c -o -fPIC libHelloWorld.so
 */

public class DriverHandler {

    //Type enums:
    public enum MotorDirection{
        UP(1),
        STOP(0),
        DOWN(-1);
        private final int directionIndex;
        MotorDirection(int index){directionIndex = index;}
    }
    public enum Button{
        OUTSIDE_UP(0),
        OUTSIDE_DOWN(1),
        INTERNAL(2);
        private final int buttonIndex;
        Button(int index){buttonIndex = index;}
    }

    //Final constants:
    private static final int MOTOR_SPEED = 2800;
    public static final int N_FLOORS = 4;
    public static final int N_BUTTONS = 3;

    //Helper method for the public API:
    //TODO: Replace with floorNotValidException.
    private static boolean isFloorValid(int floor){
        if(floor > -1 && floor < N_FLOORS){
            return true;
        }else{
            System.out.println("Floor was not valid: "+floor);
            return false;
        }
    }

    //Public API:
    public static void init() {
        if(!DriverBridge.io_init()){
            System.out.println("Could not initialize IO");
            return;
        }

        for (int floor = 0; floor < N_FLOORS; floor++) {
            for(Button b : Button.values()){
                setButtonLamp(b, floor, false);
            }
        }

        setStopLamp(false);
        setDoorOpenLamp(false);
        setFloorIndicator(0);
    }

    public static void setMotorDirection(MotorDirection direction) {
        switch (direction){
            case STOP:
                DriverBridge.io_write_analog(DriverChannels.MOTOR, 0);
                return;
            case DOWN:
                System.out.println("Setting motor direction to down");
                DriverBridge.io_set_bit(DriverChannels.MOTORDIR);
                DriverBridge.io_write_analog(DriverChannels.MOTOR, MOTOR_SPEED);
                return;
            case UP:
                DriverBridge.io_clear_bit(DriverChannels.MOTORDIR);
                DriverBridge.io_write_analog(DriverChannels.MOTOR,MOTOR_SPEED);
        }
    }


    public static void setButtonLamp(Button button, int floor, boolean turnOn) {
        if(!isFloorValid(floor)){
            return;
        }

        if (turnOn) {
            DriverBridge.io_set_bit(DriverChannels.LAMP_CHANNEL_MATRIX[floor][button.buttonIndex]);
        } else {
            DriverBridge.io_clear_bit(DriverChannels.LAMP_CHANNEL_MATRIX[floor][button.buttonIndex]);
        }
    }


    public static void setFloorIndicator(int floor) {
        if(!isFloorValid(floor)){
            return;
        }

        // Binary encoding. One light must always be on.
        if ((floor & 0x02) == 2) {
            DriverBridge.io_set_bit(DriverChannels.LIGHT_FLOOR_IND1);
        } else {
            DriverBridge.io_clear_bit(DriverChannels.LIGHT_FLOOR_IND1);
        }

        if ((floor & 0x01) == 1) {
            DriverBridge.io_set_bit(DriverChannels.LIGHT_FLOOR_IND2);
        } else {
            DriverBridge.io_clear_bit(DriverChannels.LIGHT_FLOOR_IND2);
        }
    }


    public static void setDoorOpenLamp(boolean turnOn) {
        if (turnOn) {
            DriverBridge.io_set_bit(DriverChannels.LIGHT_DOOR_OPEN);
        } else {
            DriverBridge.io_clear_bit(DriverChannels.LIGHT_DOOR_OPEN);
        }
    }


    public static void setStopLamp(boolean turnOn) {
        if (turnOn) {
            DriverBridge.io_set_bit(DriverChannels.LIGHT_STOP);
        } else {
            DriverBridge.io_clear_bit(DriverChannels.LIGHT_STOP);
        }
    }



    public static boolean isButtonPressed(Button button, int floor) {
        if(!isFloorValid(floor)){
            return false;
        }

        return DriverBridge.io_read_bit(DriverChannels.BUTTON_CHANNEL_MATRIX[floor][button.buttonIndex]) == 1;
    }

    public static int getElevatorFloor() {
        if (DriverBridge.io_read_bit(DriverChannels.SENSOR_FLOOR1) == 1) {
            return 0;
        } else if (DriverBridge.io_read_bit(DriverChannels.SENSOR_FLOOR2) == 1) {
            return 1;
        } else if (DriverBridge.io_read_bit(DriverChannels.SENSOR_FLOOR3) == 1) {
            return 2;
        } else if (DriverBridge.io_read_bit(DriverChannels.SENSOR_FLOOR4) == 1) {
            return 3;
        } else {
            return -1;
        }
    }


    public static boolean isStopPressed() {
        return DriverBridge.io_read_bit(DriverChannels.STOP) == 1;
    }

    public static boolean isObstructionPressed() {
        return DriverBridge.io_read_bit(DriverChannels.OBSTRUCTION) == 1;
    }

}