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
    AtomicLong lastEventTime = new AtomicLong();
    private static OperativeManager sOperativeManager;
    private Thread managerThread;
    private AtomicBoolean active = new AtomicBoolean();

    private OperativeManager(){
        SystemData.get().getLocalElevator().addElevatorPositionListener(this);
    }
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
    public void onDoorOpenChanged(boolean newOpen){}
    @Override
    public void onFloorChanged(Floor newFloor) {
        if (newFloor == null) return;
        newEvent();
    }
    @Override
    public void onMotorDirectionChanged(Direction newDirection){
        if (newDirection == null)return;
        if (newDirection != Direction.NONE){
            setActive(true); newEvent();
        }
        else{
            setActive(false); newEvent();
        }
    }
    @Override
    public void onOrderDirectionChanged(Direction newDirection){}
    private class ManagerThread extends Thread{
        @Override public void run(){
            try{
                Log.i(this, "Operative monitoring started");
                boolean operative = true;
                SystemData.get().getLocalElevator().setOperable(true);
                while (true){
                    if (System.currentTimeMillis()-lastEventTime.get() > 5000 && active.get()){
                        SystemData.get().getLocalElevator().setOperable(false);
                        operative = false;
                    }
                    else{
                        if (!operative) SystemData.get().getLocalElevator().setOperable(true);
                    }
                    Thread.sleep(500);
                }
            }
            catch(Exception e){
                Log.e(this, e);
            }
        }
    }
}
