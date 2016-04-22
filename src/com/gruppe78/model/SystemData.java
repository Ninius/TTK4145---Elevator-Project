package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Because SystemData is synchronized there should be as little interaction with it as possible.
 */
public class SystemData {
    private static SystemData sSystemData;
    private final List<Elevator> mElevators;
    private final Elevator mLocalElevator;

    private final Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][2];
    private final CopyOnWriteArrayList<OrderListener> orderListeners = new CopyOnWriteArrayList<>();

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Needs to be initialized with a local elevator.
     ****************************************************************************************************************/

    private SystemData(ArrayList<Elevator> elevators, Elevator localElevator){
        if(localElevator == null || elevators == null) throw new NullPointerException();
        mElevators = Collections.unmodifiableList(elevators);
        mLocalElevator = localElevator;
    }

    public static void init(ArrayList<Elevator> elevators, Elevator localElevator) {
        if (sSystemData != null) return;
        sSystemData = new SystemData(elevators, localElevator);
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
            if(elevator.getAddress().equals(inetAddress)) return elevator;
        }
        return null;
    }

    /***************************************************************************************************************
     * Listeners for order event.
     ***************************************************************************************************************/

    public void addOrderEventListener(OrderListener listener){
        orderListeners.add(listener);
    }
    public void removeOrderEventListener(OrderListener listener){
        orderListeners.remove(listener);
    }

    /***************************************************************************************************************
     * Orders - Add/Clear/Replace returns true if they were successful and listeners is called.
     ***************************************************************************************************************/
    public void addOrder(Order order){
        if (order == null) return;
        if(order.getButton() == Button.INTERNAL) order.getElevator().addInternalOrder(order.getFloor());
        else addGlobalOrder(order);
    }
    public void clearOrder(Order order){
        if (order == null) return;
        if(order.getButton() == Button.INTERNAL) order.getElevator().clearInternalOrder(order.getFloor());
        else clearGlobalOrder(order.getFloor(), order.getButton().isUp());
    }

    public void clearAllOrders(Floor floor, Elevator elevator){
        if (elevator == null || floor == null) return;
        clearGlobalOrder(floor, true);
        clearGlobalOrder(floor, false);
        elevator.clearInternalOrder(floor);
    }

    public Order getGlobalOrder(Floor floor, boolean buttonUp){
        if (floor == null) return null;
        synchronized (globalOrders){
            return globalOrders[floor.index][buttonUp ? 0 : 1];
        }
    }

    void addGlobalOrder(Order order){
        if (order == null) return;
        if(order.getFloor().isBottom() && order.getButton().isDown()) return;
        if(order.getFloor().isTop() && order.getButton().isUp()) return;

        synchronized (globalOrders){
            if(globalOrders[order.getFloor().index][order.getButton().isUp() ? 0 : 1] != null) return;
            globalOrders[order.getFloor().index][order.getButton().isUp() ? 0 : 1] = order;
        }
        for(OrderListener listener : orderListeners){
            listener.onOrderAdded(order);
        }
    }

    void clearGlobalOrder(Floor floor, boolean buttonUp){
        Order order;
        synchronized (globalOrders){
            order = globalOrders[floor.index][buttonUp ? 0 : 1];
            if(order == null) return;
            globalOrders[floor.index][buttonUp ? 0 : 1] = null;
        }

        for(OrderListener listener : orderListeners){
            listener.onOrderRemoved(order);
        }
    }
    public Order[][] getAllGlobalOrders(){
        synchronized (globalOrders){
            return globalOrders;
        }
    }
}
