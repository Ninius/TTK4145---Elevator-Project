package com.gruppe78;

import com.gruppe78.model.*;
import com.sun.org.apache.xpath.internal.operations.Mod;

/**
 * Created by oysteikh on 4/11/16.
 */
public class OrderHandler {
    public static int getNumberOfGlobalOrders(){
        int orders = 0;
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            for (int j = 0; j < Button.NUMBER_OF_BUTTONS - 1; j++){
                if (Model.get().getGlobalOrders(i, j) != null && Model.get().getGlobalOrders(i, j).getElevator() == Model.get().getLocalElevator()){
                    orders++;
                }
            }
        }
        return orders;
    }

    public static boolean validOrderExists(Floor floor){
        if (Model.get().getLocalElevator().getInternalOrder(floor.index) != null){
            return true;
        }
        if (Model.get().getLocalElevator().getDirection() == 1 && Model.get().getGlobalOrders(floor.index, 0) != null &&
                Model.get().getGlobalOrders(floor.index, 0).getElevator() == Model.get().getLocalElevator()){
            return true;
        }
        if (Model.get().getLocalElevator().getDirection() == -1 && Model.get().getGlobalOrders(floor.index, 1) != null &&
                Model.get().getGlobalOrders(floor.index, 1).getElevator() == Model.get().getLocalElevator()){
            return true;
        }
        return false;
    }

    public static void addGlobalOrder(Order order){
        if(order.getType() == Button.INTERNAL) return;
        if(Model.get().getGlobalOrders(order.getFloor().index, order.getType().index)!= null) return;

        //Order newOrder = new Order(getMinCostElevator(order), order.getType(), order.getFloor());
        Model.get().setGlobalOrders(order, order.getFloor().index, order.getType().index);
    }

    public static void clearGlobalOrder(Floor floor){
        Model.get().setGlobalOrders(null, floor.index, 0);
        Model.get().setGlobalOrders(null, floor.index, 1);
    }

    public static void reassignElevatorOrders(Elevator elevator){
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            for (int j = 0; j < Button.NUMBER_OF_BUTTONS - 1; j++){
                if (Model.get().getGlobalOrders(i, j) != null && Model.get().getGlobalOrders(i, j).getElevator() == elevator){
                    Order tempOrder = Model.get().getGlobalOrders(i, j);
                    Model.get().setGlobalOrders(null, i, 1);
                    addGlobalOrder(tempOrder);
                    //Reassign order
                }
            }
        }
    }

    public static Order getNextOrder(){
        Order nextOrder;
        int direction = Model.get().getLocalElevator().getDirection();
        if (direction == 0){
            direction = 1;
        }
        if (direction == 1){
            for (int i = Floor.NUMBER_OF_FLOORS; i >= 0; i--){
                if (Model.get().getLocalElevator().getInternalOrder(i) != null){
                    return Model.get().getLocalElevator().getInternalOrder(i);
                }
                else if(Model.get().getGlobalOrders(i, 0) != null && Model.get().getGlobalOrders(i, 0).getElevator() == Model.get().getLocalElevator()){
                    return Model.get().getGlobalOrders(i, 0);
                }
                else if(Model.get().getGlobalOrders(i, 1) != null && Model.get().getGlobalOrders(i, 1).getElevator() == Model.get().getLocalElevator()){
                    return Model.get().getGlobalOrders(i, 1);
                }
            }
        }
        else{
            for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
                if (Model.get().getLocalElevator().getInternalOrder(i) != null){
                    return Model.get().getLocalElevator().getInternalOrder(i);
                }
                else if(Model.get().getGlobalOrders(i, 1) != null && Model.get().getGlobalOrders(i, 1).getElevator() == Model.get().getLocalElevator()){
                    return Model.get().getGlobalOrders(i, 1);
                }
                else if(Model.get().getGlobalOrders(i, 0) != null && Model.get().getGlobalOrders(i, 0).getElevator() == Model.get().getLocalElevator()){
                    return Model.get().getGlobalOrders(i, 0);
                }
            }
        }
        return null;
    }
    public static int getNumberOfInternalOrders(){
        int orders = 0;
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            if (Model.get().getLocalElevator().getInternalOrder(i) != null){
                orders++;
            }
        }
        return orders;
    }
}
