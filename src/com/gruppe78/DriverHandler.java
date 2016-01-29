package com.gruppe78;

/**
 * DriverHandler works as a bridge between the driver library written in C and the rest of the application.
 * It offers enum types and booleans to ensure that the rest of the application works within bounds of the elevator.
 *
 * Compile name.c file as library: cc -G -I/java/include -I/java/include/linux name.c -o -fPIC libHelloWorld.so
 */
public class DriverHandler {
    private static final String LIBRARY_PATH = "/home/student/Gruppe123/IntellijProjects/libDriverHandler.so";
    static{
        try{
            System.load(LIBRARY_PATH);
            driverInit();
        }catch (UnsatisfiedLinkError error){
            System.out.println("Could not locate driver library at: "+LIBRARY_PATH);
        }
    }

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

    private static native void driverInit();

    private static native void setMotorDirection(int direction);
    public static void setMotorDirection(MotorDirection direction){
        setMotorDirection(direction.directionIndex);
    }

    private static native void setButtonLamp(int btnIndex, int floor, int value);
    public static void setButtonLamp(Button button, int floor, boolean lampOn){
        setButtonLamp(button.buttonIndex, floor, lampOn ? 0 : 1);
    }

    public static native void setFloorLamp(int floor);

    private static native void setDoorOpenLamp(boolean lampOn);

    private static native void setStopLamp(boolean lampOn);

    private static native int getButtonSignal(int buttonIndex, int floor);
    public static boolean getButtonSignal(Button button, int floor){
        return getButtonSignal(button.buttonIndex,floor) == 1;
    }

    public static native int getElevatorFloorSignal(); //Returns -1 if not at floor.

    private static native boolean isStopPressed();

    private static native boolean isElevatorObstructed();
}