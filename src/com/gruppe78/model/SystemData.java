package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Because SystemData is synchronized there should be as little interaction with it as possible.
 */
public class SystemData {
    private static SystemData sSystemData;
    private final List<Elevator> mElevators;
    private Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS - 1];

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Initialization of elevators must be done before.
     ****************************************************************************************************************/

    private SystemData(ArrayList<Elevator> elevators){
        mElevators = Collections.unmodifiableList(elevators);
    }

    public static void init(ArrayList<Elevator> elevators) {
        if (sSystemData != null) return;
        sSystemData = new SystemData(elevators);
    }

    public static SystemData get(){
        return sSystemData;
    }

    /***************************************************************************************************************
     * Elevators
     ***************************************************************************************************************/

    public List<Elevator> getElevatorList(){
        return mElevators;
    }

    public Elevator getLocalElevator(){
        for(Elevator elevator : mElevators){
            if(elevator.isLocal()) return elevator;
        }
        return null;
    }
    public Elevator getElevator(InetAddress inetAddress){
        for(Elevator elevator : mElevators){
            if(elevator.getInetAddress().equals(inetAddress)) return elevator;
        }
        return null;
    }

    /***************************************************************************************************************
     * Orders
     ***************************************************************************************************************/
    public void addGlobalOrder(Order order){
        if(order.getType() == Button.INTERNAL) return;
        if(globalOrders[order.getFloor().index][order.getType().index] != null) return;

        globalOrders[order.getFloor().index][order.getType().index] = order;
    }
    public synchronized Order getGlobalOrder(Floor floor, Button button){
        if(button == Button.INTERNAL) return null;
        return globalOrders[floor.index][button.index];
    }
    public synchronized void clearGlobalOrder(Floor floor, Button button){
        if(button == Button.INTERNAL) return;
        globalOrders[floor.index][button.index] = null;
    }
}
