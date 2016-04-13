package com.gruppe78;

import com.gruppe78.model.*;

/**
 * Created by oysteikh on 3/4/16.
 */

//Move to Elevator class?
public class CostFunction {
    static int costFunction(Order order){
        if (SystemData.get().getLocalElevator().isOperable()  == false){
            return Integer.MAX_VALUE;
        }
        int cost = 0;
        Elevator localElevator = SystemData.get().getLocalElevator();

        Direction directionToOrder = localElevator.getLastKnownFloor().directionTo(order.getFloor());
        if (directionToOrder != localElevator.getOrderDirection() && directionToOrder != Direction.NONE){
            cost += Floor.NUMBER_OF_FLOORS;
        }
        if (!order.getButton().matchDirection(localElevator.getOrderDirection())){
            cost += (Floor.NUMBER_OF_FLOORS-1);
        }
        cost += localElevator.getLastKnownFloor().lengthTo(order.getFloor());
        cost += OrderHandler.getNumberOfInternalOrders(localElevator)*Floor.NUMBER_OF_FLOORS/2;
        cost += OrderHandler.getNumberOfGlobalOrders(localElevator)*Floor.NUMBER_OF_FLOORS/2;
        return cost;


        /* Old implementation */
        /*int cost = 0; int direction = 0;
        int elevFloor; int elevDir;
        for (Elevator elevator : SystemData.get().getElevatorList()) {
            elevFloor = elevator.getFloor();
            elevDir = elevator.getOrderDirection();
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
