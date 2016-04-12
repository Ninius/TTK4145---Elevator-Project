package com.gruppe78;

import com.gruppe78.model.Floor;
import com.gruppe78.model.SystemData;

/**
 * Created by oysteikh on 4/12/16.
 */

//Implement as listener?
public class OperativeManager {
    long lastEventTime;
    private static OperativeManager sOperativeManager;
    private Thread managerThread;

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
        lastEventTime = System.currentTimeMillis();
    }
    public void start(){
        managerThread = new ManagerThread();
        managerThread.setName(ManagerThread.class.getSimpleName());
        managerThread.start();
    }
    private class ManagerThread extends Thread{
        @Override public void run(){
            try{
                while (true){
                    if (System.currentTimeMillis()-lastEventTime > 4000){
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
