package com.gruppe78;

import com.gruppe78.model.Floor;
import com.gruppe78.model.SystemData;

/**
 * Created by oysteikh on 4/12/16.
 */

//Implement as listener?
public class AliveManager {
    long lastEventTime;
    private static AliveManager sAliveManager;
    private Thread managerThread;

    private AliveManager(){
        lastEventTime = System.currentTimeMillis();
    };
    public static AliveManager get(){
        if(sAliveManager == null){
            sAliveManager = new AliveManager();
        }
        return sAliveManager;
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
                        //SystemData.get().getLocalElevator().setAlive(false);
                    }
                    else{
                        //SystemData.get().getLocalElevator().setAlive(true)
                    }
                    Thread.sleep(500);
                }
            }
            catch(Exception e){

            }
        }
    }
}
