package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;
import com.gruppe78.model.Order;

import java.util.ArrayList;

/**
 * Created by oysteikh on 3/4/16.
 */

//Move to Elevator class?
public class CostFunction {
    static int costFunction(Order order){
        int orderDirection = order.getFloor().index - Model.get().getLocalElevator().getFloor().index;
        int cost = 0;
        if (orderDirection != Model.get().getLocalElevator().getDirection() && orderDirection != 0){
            cost += Floor.NUMBER_OF_FLOORS;
        }
        if (order.getType().getDirection() != Model.get().getLocalElevator().getDirection()){
            cost += (Floor.NUMBER_OF_FLOORS-1);
        }
        cost += (order.getFloor().index - Model.get().getLocalElevator().getFloor().index);
        cost += Model.get().getLocalElevator().getNumberOfInternalOrders()*Floor.NUMBER_OF_FLOORS/2;
        cost += Model.get().getNumberOfGlobalOrders()*Floor.NUMBER_OF_FLOORS/2;
        return cost;


        /* Old implementation */
        /*int cost = 0; int direction = 0;
        int elevFloor; int elevDir;
        for (Elevator elevator : Model.get().getElevatorList()) {
            elevFloor = elevator.getFloor();
            elevDir = elevator.getDirection();
            if (floor > elevFloor){
                direction = 1;
            }
            else if (floor == elevFloor){
                direction = 0;
            }
            else {
                direction = -1;
            }
            if (elevDir == 0 || elevDir == direction){
                cost += 1;
            }
            else if (direction != elevDir && elevDir != 0){
                cost += Floor.NUMBER_OF_FLOORS;
            }
            cost += Math.abs(elevFloor-floor);
            if (cost < minCost){
                minCost = cost;
                minCostElevator = elevator;
            }
            cost = 0;
        }
        return minCostElevator;*/
    }
}
