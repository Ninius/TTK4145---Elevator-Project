package com.gruppe78.model;

import java.util.ArrayList;

/**
 * Because Model is synchronized there should be as little interaction with it as possible.
 * Maybe replace the Singleton pattern with static methods since there may be little use of polymorphism capabilities here.
 */
public class Model {
    private static Model sModel = new Model();
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS - 1];

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Eager initialization.
     ****************************************************************************************************************/

    private Model(){

    }

    public static Model get(){
        return sModel;
    }

    /***************************************************************************************************************
     * The list of elevators
     ***************************************************************************************************************/

    public synchronized void addElevator(Elevator elevator){
        elevators.add(elevator);
    }

    public synchronized void removeElevator(Elevator elevator){
        elevators.remove(elevator);
    }

    public synchronized ArrayList<Elevator> getElevatorList(){
        return elevators;
    }

    public synchronized Elevator getLocalElevator(){
        for(Elevator elevator : elevators){
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
    public void addGlobalOrder(Order order){
        if(order.getType() == Button.INTERNAL) return;
        //Order newOrder = new Order(costFunction(), order.getType(), order.getFloor());
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
        return null;
    }
}
