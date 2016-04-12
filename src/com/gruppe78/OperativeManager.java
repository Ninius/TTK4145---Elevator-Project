package com.gruppe78;

import com.gruppe78.model.Floor;
import com.gruppe78.model.SystemData;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by oysteikh on 4/12/16.
 */

//Implement as listener?
public class OperativeManager {
    AtomicLong lastEventTime;
    private static OperativeManager sOperativeManager;
    private Thread managerThread;
    private AtomicBoolean active;

    private OperativeManager(){
        lastEventTime = System.currentTimeMillis();
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
    private class ManagerThread extends Thread{
        @Override public void run(){
            try{
                while (true){
                    if (System.currentTimeMillis()-lastEventTime.get() > 4000 && active.get()){
                        SystemData.get().getLocalElevator().setOperable(false);
                    }
                    else{
                        SystemData.get().getLocalElevator().setOperable(true);
                    }
                    Thread.sleep(500);
                }
            }
            catch(Exception e){

            }
        }
    }
}
