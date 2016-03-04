package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.Button;
import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;

/**
 * Pings the driver for button and sensor events and sends info to Model if something has changed.
 *
 * Possibly start two independent threads for the pinging.
 */
@SuppressWarnings("InfiniteLoopStatement")
public class ElevatorEventHandler{
    private static ElevatorEventHandler sElevatorEventHandler;
    private Elevator mElevator;
    private IOPingerThread thread;

    //Settings:
    private static final long SLEEP_TIME = 200;

    //Holds the last value such that the synchronized methods of the data object is only called upon change.
    private boolean[][] buttonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];
    Floor lastFloor = DriverHandler.getElevatorFloor();


    /******************************************************************************************************
     * Constructing, initializing and referencing
     ******************************************************************************************************/

    private ElevatorEventHandler(Elevator elevator){
        mElevator = elevator;
        thread = new IOPingerThread();
        thread.start();
    }
    public static void init(Elevator localElevator){
        if(sElevatorEventHandler != null) return;
        sElevatorEventHandler = new ElevatorEventHandler(localElevator);
    }

    public static ElevatorEventHandler get(){
        return sElevatorEventHandler;
    }

    private class IOPingerThread extends Thread{

        @Override
        public void run(){
            try {
                while(true){
                    for(Button button : Button.values()){
                        for(Floor floor : Floor.values()){
                            if(floor.isBottom() && button == Button.OUTSIDE_DOWN) continue;
                            if(floor.isTop() && button == Button.OUTSIDE_UP) continue;

                            //Checks if something has changed to relieve stress on the synchronized data object.
                            boolean pressed = DriverHandler.isButtonPressed(button, floor);
                            if(buttonPressed[floor.index][button.index] != pressed){
                                buttonPressed[floor.index][button.index] = pressed;
                                mElevator.setButtonPressed(floor,button,pressed);
                            }
                        }
                    }

                    Floor floor = DriverHandler.getElevatorFloor();
                    if(lastFloor != floor){
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
