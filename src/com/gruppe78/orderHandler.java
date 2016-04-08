package com.gruppe78;

import com.gruppe78.model.Order;

public class orderHandler {
    private boolean[] callUp;
    private boolean[] callDown;
    private boolean[] localOrders;
    
    public orderHandler(int numFloor){
        callUp = new boolean[numFloor];
        callDown = new boolean[numFloor];
        localOrders = new boolean [numFloor];
    }
    
    //-1=down, 0=local, 1=up
    public void newOrder(int dir, int floor) {
        switch (dir){
            case -1: callUp[floor] = true;
            case 0: localOrders[floor] = true;
            case 1: callUp[floor] = true;
        }
    }

    public Order getNextOrder(){
        return null;
    }
    
    public void clearOrder(int dir, int floor){
        switch (dir){
            case -1: callDown[floor] = false;
            case 1: callUp[floor] = false;
        }
    }
}