package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;
import com.gruppe78.utilities.LoopThread;

/**
 * Pings the driver for button and sensor events and sends info to SystemData if something has changed.
 *
 * Possibly start two independent threads for the pinging.
 */
@SuppressWarnings("InfiniteLoopStatement")
public class LocalElevatorInputChecker {
    private static LocalElevatorInputChecker sLocalElevatorInputChecker;
    private Elevator mElevator;
    private ButtonCheckThread buttonCheckThread;
    private FloorCheckThread floorCheckThread;

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
        buttonCheckThread = new ButtonCheckThread();
        buttonCheckThread.setName(ButtonCheckThread.class.getSimpleName());
        buttonCheckThread.start();

        floorCheckThread = new FloorCheckThread();
        floorCheckThread.setName(FloorCheckThread.class.getSimpleName());
        floorCheckThread.start();
    }

    /**************************************************************
     * Checker Threads
     **************************************************************/

    private class FloorCheckThread extends LoopThread{
        private Floor lastFloor = DriverHelper.getElevatorFloor();

        @Override
        public void loopRun() {
            Floor floor = DriverHelper.getElevatorFloor();
            if(lastFloor == floor) return;

            lastFloor = floor;
            mElevator.setFloor(floor);
            Log.i(this, "Floor changed: "+floor);
        }

        @Override
        public int getInterval() {
            return FLOOR_SLEEP_TIME;
        }
    }
    private class ButtonCheckThread extends LoopThread{
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

                    Log.i(this, "Button changed:"+button+ " on floor "+floor+" - pressed: "+pressed);

                    if(pressed) OrderHandler.addOrder(new Order(SystemData.get().getLocalElevator(), button, floor));
                }
            }
        }

        @Override
        public int getInterval() {
            return BUTTON_SLEEP_TIME;
        }
    }
}
