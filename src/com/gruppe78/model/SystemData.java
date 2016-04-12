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
    private final Elevator mLocalElevator;

    private final Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][2];
    private final ArrayList<OrderEventListener> orderEventListeners = new ArrayList<>();

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Needs to be initialized with a local elevator.
     ****************************************************************************************************************/

    private SystemData(ArrayList<Elevator> externalElevators, Elevator localElevator){
        if(localElevator == null) throw new NullPointerException();
        externalElevators = new ArrayList<>(externalElevators);
        externalElevators.add(localElevator);
        mElevators = Collections.unmodifiableList(externalElevators);
        mLocalElevator = localElevator;
    }

    public static void init(ArrayList<Elevator> externalElevators, Elevator localElevator) {
        if (sSystemData != null) return;
        sSystemData = new SystemData(externalElevators, localElevator);
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
        return mLocalElevator;
    }
    public Elevator getElevator(InetAddress inetAddress){
        for(Elevator elevator : mElevators){
            if(elevator.getInetAddress().equals(inetAddress)) return elevator;
        }
        return null;
    }

    /***************************************************************************************************************
     * Listeners for order event.
     ***************************************************************************************************************/

    public void addOrderEventListener(OrderEventListener listener){
        synchronized (orderEventListeners){
            orderEventListeners.add(listener);
        }
    }
    public void removeOrderEventListener(OrderEventListener listener){
        synchronized (orderEventListeners) {
            orderEventListeners.remove(listener);
        }
    }

    /***************************************************************************************************************
     * Orders - Add/Clear/Replace returns true if they were successful and listeners is called.
     ***************************************************************************************************************/

    public boolean addGlobalOrder(Order order){
        if(order.getButton() == Button.INTERNAL) return false;
        if(order.getFloor().isBottom() && order.getButton().isDown()) return false;
        if(order.getFloor().isTop() && order.getButton().isUp()) return false;

        synchronized (globalOrders){
            if(globalOrders[order.getFloor().index][order.getButton().isUp() ? 0 : 1] != null) return false;
            globalOrders[order.getFloor().index][order.getButton().isUp() ? 0 : 1] = order;
        }

        synchronized (orderEventListeners){
            for(OrderEventListener listener : orderEventListeners){
                listener.onOrderAdded(order);
            }
        }
        return true;
    }

    public boolean clearGlobalOrder(Floor floor, boolean buttonUp){
        Order order;
        synchronized (globalOrders){
            order = globalOrders[floor.index][buttonUp ? 0 : 1];
            globalOrders[floor.index][buttonUp ? 0 : 1] = null;
        }
        if(order == null) return false;

        synchronized (orderEventListeners){
            for(OrderEventListener listener : orderEventListeners){
                listener.onOrderRemoved(order);
            }
        }
        return true;
    }

    public Order getGlobalOrder(Floor floor, boolean buttonUp){
        synchronized (globalOrders){
            return globalOrders[floor.index][buttonUp ? 0 : 1];
        }
    }

}
