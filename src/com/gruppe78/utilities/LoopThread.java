package com.gruppe78.utilities;

/**
 * Created by jespe on 15.04.2016.
 */
public abstract class LoopThread extends Thread {

    @Override public void run(){
        beforeLoop();
        int sleepTime = getInterval();
        try{
            while(!interrupted()){
                loopRun();
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        afterLoop();
    }
    public abstract void loopRun();
    public abstract int getInterval();
    public void beforeLoop(){

    }
    public void afterLoop(){

    }
}
