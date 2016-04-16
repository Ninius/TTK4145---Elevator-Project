package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.utilities.LoopThread;

/**
 * Pings the driver for button and sensor events and sends info to SystemData if something has changed.
 *
 * //TODO: Separate into two classes?
 */
public class LocalElevatorInputChecker {
    private static LocalElevatorInputChecker sLocalElevatorInputChecker;
    private Elevator mElevator;
    private ButtonCheck buttonCheck;
    private FloorCheck floorCheck;

    //Settings:
    private static final int FLOOR_SLEEP_TIME = 200;
    private static final int BUTTON_SLEEP_TIME = 100;


    /******************************************************************************************************
     * Constructing, initializing and referencing
     ******************************************************************************************************/

    private LocalElevatorInputChecker(){
        mElevator = SystemData.get().getLocalElevator();
    }
    public static LocalElevatorInputChecker get(){
        if(sLocalElevatorInputChecker == null){
            sLocalElevatorInputChecker = new LocalElevatorInputChecker();
        }
        return sLocalElevatorInputChecker;
    }
    public void start(){
        buttonCheck = new ButtonCheck();
        buttonCheck.setName(ButtonCheck.class.getSimpleName());
        buttonCheck.start();

        floorCheck = new FloorCheck();
        floorCheck.setName(FloorCheck.class.getSimpleName());
        floorCheck.start();
    }

    /**************************************************************
     * Checker Threads
     **************************************************************/

    private class FloorCheck extends LoopThread{
        private Floor lastFloor = SystemData.get().getLocalElevator().getFloor();

        @Override
        public void loopRun() {
            Floor floor = DriverHelper.getElevatorFloor();
            if(lastFloor == floor || floor == null) return; //TODO: Is null valid floor?
            lastFloor = floor;
            mElevator.setFloor(floor);
        }

        @Override
        public int getInterval() {
            return FLOOR_SLEEP_TIME;
        }
    }
    private class ButtonCheck extends LoopThread{
        private boolean[][] buttonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];

        @Override
        public void loopRun() {
            for (Button button : Button.values()) {
                for (Floor floor : Floor.values()) {
                    if (floor.isBottom() && button == Button.OUTSIDE_DOWN) continue;
                    if (floor.isTop() && button == Button.OUTSIDE_UP) continue;

                    boolean pressed = DriverHelper.isButtonPressed(button, floor);
                    if(buttonPressed[floor.index][button.index] == pressed) continue;

                    buttonPressed[floor.index][button.index] = pressed;

                    if(pressed) OrderHandler.onButtonPressed(button, floor);
                }
            }
        }

        @Override
        public int getInterval() {
            return BUTTON_SLEEP_TIME;
        }
    }
}
