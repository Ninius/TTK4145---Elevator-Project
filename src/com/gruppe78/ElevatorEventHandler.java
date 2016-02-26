package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.Button;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;

import java.io.IOException;

/**
 * Pings the driver for button and sensor events and sends info to Model if something has changed.
 */
public class ElevatorEventHandler extends Thread{

    @Override
    public void run(){
        while(true){
            for(Button button : Button.values()){
                for(Floor floor : Floor.values()){
                    if(floor.isBottom() && button == Button.OUTSIDE_DOWN) continue;
                    if(floor.isTop() && button == Button.OUTSIDE_UP) continue;
                    if(DriverHandler.isButtonPressed(button,floor.index)){

                    }
                }
            }
        }
    }
}
