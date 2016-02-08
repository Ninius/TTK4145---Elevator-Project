package com.gruppe78;

import com.gruppe78.driver.DriverHandler;

public class Main {
    public static void main(String[] args) {

        System.out.println("System started");
        DriverHandler.init();
        while (true){
            try {
                Thread.sleep(200);
                if(DriverHandler.isStopPressed()){
                    System.out.println("Stop is pressed");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
