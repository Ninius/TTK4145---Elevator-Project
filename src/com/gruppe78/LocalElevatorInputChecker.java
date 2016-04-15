package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;

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
    private static final long FLOOR_SLEEP_TIME = 400;
    private static final long BUTTON_SLEEP_TIME = 100;


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

    private class FloorCheckThread extends Thread{
        @Override public void run(){
            try {
                Floor lastFloor = DriverHelper.getElevatorFloor();
                while(true){
                    Floor floor = DriverHelper.getElevatorFloor();
                    if(lastFloor != floor){
                        lastFloor = floor;
                        mElevator.setFloor(floor);
                    }
                    Thread.sleep(FLOOR_SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private class ButtonCheckThread extends Thread{
        @Override public void run(){
            try{
                boolean[][] buttonPressed = new boolean[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS];
                while(true) {
                    for (Button button : Button.values()) {
                        for (Floor floor : Floor.values()) {
                            if (floor.isBottom() && button == Button.OUTSIDE_DOWN) continue;
                            if (floor.isTop() && button == Button.OUTSIDE_UP) continue;

                            boolean pressed = DriverHelper.isButtonPressed(button, floor);
                            if (buttonPressed[floor.index][button.index] != pressed) {
                                buttonPressed[floor.index][button.index] = pressed;
                                OrderHandler.addOrder(new Order(SystemData.get().getLocalElevator(), button, floor));
                                //TODO: Do something.
                            }
                        }
                    }
                    Thread.sleep(BUTTON_SLEEP_TIME);
                }
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
