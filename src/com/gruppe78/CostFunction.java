package com.gruppe78;

import com.gruppe78.model.*;

/**
 * Created by oysteikh on 3/4/16.
 */

//Move to Elevator class?
public class CostFunction {//TODO: Test
    private static int costFunction(Elevator elevator, Button button, Floor floor){
        if (!elevator.isOperable() || !elevator.isConnected() || elevator.getFloor() == null){
            return Integer.MAX_VALUE;
        }
        int cost = 0;
        Direction directionToOrder = elevator.getFloor().directionTo(floor);
        if (directionToOrder != elevator.getOrderDirection() && directionToOrder != Direction.NONE && elevator.getOrderDirection() != Direction.NONE){
            cost += Floor.NUMBER_OF_FLOORS;
        }
        if (!button.matchDirection(elevator.getOrderDirection())){
            cost += (Floor.NUMBER_OF_FLOORS-1);
        }
        cost += elevator.getFloor().lengthTo(floor);
        cost += OrderHandler.getNumberOfInternalOrders(elevator)*Floor.NUMBER_OF_FLOORS/2;
        cost += OrderHandler.getNumberOfGlobalOrders(elevator)*Floor.NUMBER_OF_FLOORS/2;
        return cost;

    }

    public static Elevator getMinCostElevator(Button button, Floor floor){
        Elevator localElevator = SystemData.get().getLocalElevator();
        if(!localElevator.isConnected()) return localElevator;

        Elevator minCostElevator = localElevator;
        int minCost = Integer.MAX_VALUE;
        for (Elevator elevator : SystemData.get().getElevatorList()) {
            if (elevator.isOperable() && elevator.isConnected()) {
                int cost = costFunction(elevator, button, floor);
                if (cost < minCost) {
                    minCost = cost;
                    minCostElevator = elevator;
                }
            }
        }
        return minCostElevator;
    }
}
