package com.gruppe78;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by oysteikh on 4/12/16.
 */

//Implement as listener?
public class OperativeManager implements ElevatorPositionListener{
    private static final String NAME = OperativeManager.class.getSimpleName();
    AtomicLong lastEventTime;
    private static OperativeManager sOperativeManager;
    private Thread managerThread;
    private AtomicBoolean active;

    private OperativeManager(){
        SystemData.get().getLocalElevator().addElevatorMovementListener(this);
        lastEventTime = new AtomicLong();
    };
    public static OperativeManager get(){
        if(sOperativeManager == null){
            sOperativeManager = new OperativeManager();
        }
        return sOperativeManager;
    }
    public void newEvent(){
        lastEventTime.set(System.currentTimeMillis());
    }
    public void start(){
        managerThread = new ManagerThread();
        managerThread.setName(ManagerThread.class.getSimpleName());
        managerThread.start();
    }
    public void setActive(boolean Active){
        active.set(Active);
     }

    @Override
    public void onDoorOpenChanged(boolean newOpen){};
    @Override
    public void onFloorChanged(Floor newFloor) {
        if (newFloor == null) return;
        newEvent();
        Log.i(NAME, "New event detected");
    }
    @Override
    public void onMotorDirectionChanged(Direction newDirection){
        if (newDirection != Direction.NONE){
            setActive(true); newEvent();
            Log.i(NAME, "Operative monitoring started.");
        }
        else{
            setActive(false); newEvent();
            Log.i(NAME, "Operative monitoring finished. Elevator arrived at destination.");
        }
    }
    @Override
    public void onOrderDirectionChanged(Direction newDirection){};
    private class ManagerThread extends Thread{
        @Override public void run(){
            try{
                while (true){
                    if (System.currentTimeMillis()-lastEventTime.get() > 4000 && active.get()){
                        SystemData.get().getLocalElevator().setOperable(false);
                        //Reassign global orders here
                        Log.i(NAME, "Event Timeout: Elevator not operable.");
                    }
                    else{
                        SystemData.get().getLocalElevator().setOperable(true);
                        Log.i(NAME, "Elevator operable.");
                    }
                    Thread.sleep(500);
                }
            }
            catch(Exception e){

            }
        }
    }
}
