package com.gruppe78;

import com.gruppe78.model.Elevator;
import com.gruppe78.model.Floor;
import com.gruppe78.model.Model;

import java.util.ArrayList;

/**
 * Created by oysteikh on 3/4/16.
 */
public class CostFunction {
    static Elevator costFunction(int floor, int type){
        int cost = 0; int direction = 0;
        int minCost = 10000; Elevator minCostElevator;
        int elevFloor; int elevDir;
//        ArrayList<Elevator> elevators = Model.getElevatorList();
        for (Elevator elevator : Model.getElevatorList()) {
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
        return minCostElevator;
    }
}
