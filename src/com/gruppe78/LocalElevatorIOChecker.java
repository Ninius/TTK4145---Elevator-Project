package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;

/**
 * Pings the driver for button and sensor events and sends info to SystemData if something has changed.
 *
 * Possibly start two independent threads for the pinging.
 */
@SuppressWarnings("InfiniteLoopStatement")
public class LocalElevatorIOChecker {
    private static LocalElevatorIOChecker sLocalElevatorIOChecker;
    private Elevator mElevator;
    private IOCheckThread thread;

    //Settings:
    private static final long SLEEP_TIME = 200;

    //Holds the last value such that the synchronized methods of the data object is only called upon change.
    private boolean[][] buttonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];
    Floor lastFloor = DriverHelper.getElevatorFloor();


    /******************************************************************************************************
     * Constructing, initializing and referencing
     ******************************************************************************************************/

    private LocalElevatorIOChecker(){
        mElevator = SystemData.get().getLocalElevator();
        thread = new IOCheckThread();
        thread.setName("IOCheckThread");
        thread.start();
    }
    public static void init(){
        if(sLocalElevatorIOChecker != null) return;
        sLocalElevatorIOChecker = new LocalElevatorIOChecker();
    }

    public static LocalElevatorIOChecker get(){
        return sLocalElevatorIOChecker;
    }

    private class IOCheckThread extends Thread{

        @Override
        public void run(){
            try {
                while(true){
                    for(Button button : Button.values()){
                        for(Floor floor : Floor.values()){
                            if(floor.isBottom() && button == Button.OUTSIDE_DOWN) continue;
                            if(floor.isTop() && button == Button.OUTSIDE_UP) continue;

                            //Checks if something has changed to relieve stress on the synchronized data object.
                            boolean pressed = DriverHelper.isButtonPressed(button, floor);
                            if(buttonPressed[floor.index][button.index] != pressed){
                                buttonPressed[floor.index][button.index] = pressed;
                                mElevator.setButtonPressed(floor,button,pressed);
                            }
                        }
                    }

                    Floor floor = DriverHelper.getElevatorFloor();
                    if(lastFloor != floor && floor != null){
                        lastFloor = floor;
                        mElevator.setFloor(floor);
                    }
                    Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
