package com.gruppe78.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Because Model is synchronized there should be as little interaction with it as possible.
 * Maybe replace the Singleton pattern with static methods since there may be little use of polymorphism capabilities here.
 */
public class Model {
    private static Model sModel = new Model();
    private List<Elevator> mElevators = new ArrayList<>();
    private Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS - 1];

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Initialization of elevators must be done before.
     ****************************************************************************************************************/

    private Model(ArrayList<Elevator> elevators){
        mElevators = Collections.unmodifiableList(elevators);
    }

    public static void init(ArrayList<Elevator> elevators){
        if(sModel != null) return;
        sModel = new Model(elevators);

    public static Model get(){
        return sModel;
    }

    /***************************************************************************************************************
     * The list of elevators
     ***************************************************************************************************************/

    public List<Elevator> getElevatorList(){
        return mElevators;
    }

    public synchronized Elevator getLocalElevator(){
        for(Elevator elevator : mElevators){
            if(elevator.isLocal()) return elevator;
        }
        return null;
    }

    /***************************************************************************************************************
     * Orders
     ***************************************************************************************************************/
    public int getNumberOfGlobalOrders(){
        int orders = 0;
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            for (int j = 0; j < Button.NUMBER_OF_BUTTONS - 1; j++){
                if (globalOrders[i][j] != null && globalOrders[i][j].getElevator() == getLocalElevator()){
                    orders++;
                }
            }
        }
        return orders;
    }
    public boolean orderExists(Floor floor){
        if (getLocalElevator().getInternalOrder(floor.index) != null){
            return true;
        }
        if (getLocalElevator().getDirection() == 1 && globalOrders[floor.index][0] != null && globalOrders[floor.index][0].getElevator() == getLocalElevator()){
            return true;
        }
        if (getLocalElevator().getDirection() == -1 && globalOrders[floor.index][1] != null && globalOrders[floor.index][1].getElevator() == getLocalElevator()){
            return true;
        }
        return false;
    }
    public void addGlobalOrder(Order order){
        if(order.getType() == Button.INTERNAL) return;
        if(globalOrders[order.getFloor().index][order.getType().index] != null) return;

        //Order newOrder = new Order(getMinCostElevator(order), order.getType(), order.getFloor());
        globalOrders[order.getFloor().index][order.getType().index] = order;
    }
    public void clearGlobalOrder(Floor floor){
        globalOrders[floor.index][0] = null;
        globalOrders[floor.index][1] = null;
    }

    public void reassignElevatorOrders(Elevator elevator){
        for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
            for (int j = 0; j < Button.NUMBER_OF_BUTTONS - 1; j++){
                if (globalOrders[i][j] != null && globalOrders[i][j].getElevator() == elevator){
                    Order tempOrder = globalOrders[i][j];
                    globalOrders[i][j] = null;
                    addGlobalOrder(tempOrder);
                    //Reassign order
                }
            }
        }
    }

    public Order getNextOrder(){
        Order nextOrder;
        int direction = getLocalElevator().getDirection();
        if (direction == 0){
            direction = 1;
        }
        if (direction == 1){
            for (int i = Floor.NUMBER_OF_FLOORS; i >= 0; i--){
                if (getLocalElevator().getInternalOrder(i) != null){
                    return getLocalElevator().getInternalOrder(i);
                }
                else if(globalOrders[i][0] != null && globalOrders[i][0].getElevator() == getLocalElevator()){
                    return globalOrders[i][0];
                }
                else if(globalOrders[i][1] != null && globalOrders[i][1].getElevator() == getLocalElevator()){
                    return globalOrders[i][1];
                }
            }
        }
        else{
            for (int i = 0; i < Floor.NUMBER_OF_FLOORS; i++){
                if (getLocalElevator().getInternalOrder(i) != null){
                    return getLocalElevator().getInternalOrder(i);
                }
                else if(globalOrders[i][1] != null && globalOrders[i][1].getElevator() == getLocalElevator()){
                    return globalOrders[i][1];
                }
                else if(globalOrders[i][0] != null && globalOrders[i][0].getElevator() == getLocalElevator()){
                    return globalOrders[i][0];
                }
            }
        }
        return null;
    }
}
