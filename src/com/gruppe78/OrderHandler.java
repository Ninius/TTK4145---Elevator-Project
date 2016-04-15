package com.gruppe78;

import com.gruppe78.model.*;
import com.gruppe78.network.Networker;
import com.gruppe78.utilities.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oysteikh on 4/11/16.
 */
public class OrderHandler implements ElevatorStatusListener{//TODO: Remove ElevatorStatusListener or change to Singleton

    private static OrderHandler mOrderHandler;
    private OrderHandler (){
        for (Elevator elevator : SystemData.get().getElevatorList()){
            elevator.addElevatorStatusListener(this);

        }
    }
    public static OrderHandler get(){
        if (mOrderHandler == null){
            mOrderHandler = new OrderHandler();
        }
        return mOrderHandler;
    }


    public static void addOrder(Order order){
        if (order.getButton() != Button.INTERNAL){
            newGlobalOrder(order);
            Log.i("OrderHandler", "New "+order);
        }
        else{
            SystemData.get().getLocalElevator().addInternalOrder(order.getFloor(), order.getButton());
            Log.i("OrderHandler", "New External Order " + (order.getButton().isUp()? "Up" : "Down") + order.getFloor());
        }
    }
    private static void newGlobalOrder(Order order){//TODO: Implement/change to correct minCost function call. Refactoring might be needed depending on network
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SystemData data = SystemData.get();
                if (data.getGlobalOrder(order.getFloor(), order.getButton().isUp()) == null){
                    //Elevator minCostElevator = Networker.getMinCostElevator(order); Change to correct function call
                    Elevator minCostElevator = data.getLocalElevator();
                    data.addGlobalOrder(new Order(minCostElevator, order.getButton(), order.getFloor()));
                }
                else if(!data.getLocalElevator().isConnected()){
                    data.addGlobalOrder(new Order(data.getLocalElevator(), order.getButton(), order.getFloor()));
                }

            }
        });
        thread.setName("Get minCostElevator thread");
        thread.start();
    }

    public void onConnectionChanged(Elevator elevator, boolean connected){
        if (!connected){
            List<Elevator> elevatorList = SystemData.get().getElevatorList();
            Elevator maxElevator = elevatorList.get(0);
            for (Elevator mElevator : elevatorList){
                if (mElevator.getID()>maxElevator.getID() && mElevator != elevator){
                    maxElevator = mElevator;
                }
            }
            if (maxElevator == SystemData.get().getLocalElevator()){
                reassignElevatorOrders(elevator);
            }
        }
    }
    public void onOperableChanged(Elevator elevator, boolean operable){
        if (!operable && elevator == SystemData.get().getLocalElevator()){
            reassignElevatorOrders(SystemData.get().getLocalElevator());
        }
    }
    public static int getNumberOfGlobalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                if(SystemData.get().getGlobalOrder(floor, button.isUp()) != null && SystemData.get().getGlobalOrder(floor, button.isUp()).getElevator() == elevator){
                    orders++;
                }
            }
        }
        return orders;
    }

    public static boolean isOrderOnFloor(Floor floor, Elevator elevator){
        if (elevator.getInternalOrder(floor) != null){
            return true;
        }
        if (SystemData.get().getGlobalOrder(floor, true) != null && elevator.getOrderDirection() == Direction.UP && SystemData.get().getGlobalOrder(floor, true).getElevator() == elevator){
            return true;
        }
        if (SystemData.get().getGlobalOrder(floor, false) != null && elevator.getOrderDirection() == Direction.DOWN && SystemData.get().getGlobalOrder(floor, false).getElevator() == elevator){
            return true;
        }
        return false;
    }

    public static void reassignElevatorOrders(Elevator elevator){
        SystemData data = SystemData.get();
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                if (data.getGlobalOrder(floor, button.isUp()) != null && data.getGlobalOrder(floor, button.isUp()).getElevator() == elevator){
                    Order tempOrder = data.getGlobalOrder(floor, button.isUp());
                    data.clearGlobalOrder(floor, button.isUp());
                    newGlobalOrder(tempOrder);
                    //Reassign order
                }
            }
        }
    }

    public static Order getNextOrder(Elevator elevator){ //TODO: Check.
        SystemData data = SystemData.get();
        Direction direction = elevator.getOrderDirection();
        if (direction == Direction.NONE){
            direction = Direction.UP;
        }
        Floor nextFloor = elevator.getLastKnownFloor().getNextFloor(direction);
        while(nextFloor != null){
            Order order = getAnyOrder(elevator, nextFloor);
            if(order != null) return order;
            nextFloor = nextFloor.getNextFloor(direction);
        }
        return null;
    }
    private static Order getAnyOrder(Elevator elevator, Floor floor){
        SystemData data = SystemData.get();
        if (elevator.getInternalOrder(floor) != null){
            return elevator.getInternalOrder(floor);
        }
        else if(data.getGlobalOrder(floor, false) != null && data.getGlobalOrder(floor, false).getElevator() == elevator){
            return data.getGlobalOrder(floor, false);
        }
        else if(data.getGlobalOrder(floor, true) != null && data.getGlobalOrder(floor, true).getElevator() == elevator){
            return data.getGlobalOrder(floor, true);
        }
        return null;
    }

    public static int getNumberOfInternalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            if (elevator.getInternalOrder(floor) != null){
                orders++;
            }
        }
        return orders;
    }
}
