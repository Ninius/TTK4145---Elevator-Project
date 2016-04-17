package com.gruppe78;

import com.gruppe78.model.*;

/**
 * Created by oysteikh on 3/4/16.
 */

//Move to Elevator class?
public class CostFunction {//TODO: Test
    private static int costFunction(Elevator elevator, Order order){
        if (!elevator.isOperable() || !elevator.isConnected()){
            return Integer.MAX_VALUE;
        }
        int cost = 0;
        Elevator localElevator = SystemData.get().getLocalElevator();
        Direction directionToOrder = localElevator.getFloor().directionTo(order.getFloor());
        if (directionToOrder != localElevator.getOrderDirection() && directionToOrder != Direction.NONE){
            cost += Floor.NUMBER_OF_FLOORS;
        }
        if (!order.getButton().matchDirection(localElevator.getOrderDirection())){
            cost += (Floor.NUMBER_OF_FLOORS-1);
        }
        cost += localElevator.getFloor().lengthTo(order.getFloor());
        cost += OrderHandler.getNumberOfInternalOrders(localElevator)*Floor.NUMBER_OF_FLOORS/2;
        cost += OrderHandler.getNumberOfGlobalOrders(localElevator)*Floor.NUMBER_OF_FLOORS/2;
        return cost;

    }

    public static Elevator getMinCostElevator(Order order){
        Elevator minCostElevator = SystemData.get().getLocalElevator();
        int minCost = Integer.MAX_VALUE; int cost;
        for (Elevator elevator : SystemData.get().getElevatorList()) {
            if (elevator.isOperable() && (elevator.isConnected() || elevator.isLocal())) {
                cost = costFunction(elevator, order);
                if (cost < minCost) {
                    minCost = cost;
                    minCostElevator = elevator;
                }
            }
        }
        return minCostElevator;
    }
}
