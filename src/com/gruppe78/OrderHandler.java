package com.gruppe78;

import com.gruppe78.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by oysteikh on 4/11/16.
 */
public class OrderHandler {
    public int getNumberOfGlobalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                if(SystemData.get().getGlobalOrder(floor,button) != null && SystemData.get().getGlobalOrder(floor,button).getElevator() == elevator){
                    orders++;
                }
            }
        }
        return orders;
    }

    public boolean isOrderOnFloor(Floor floor, Elevator elevator){
        if (elevator.getInternalOrder(floor) != null){
            return true;
        }
        if (SystemData.get().getGlobalOrder(floor, Button.OUTSIDE_UP) != null && elevator.getDirection() == Direction.UP && SystemData.get().getGlobalOrder(floor, Button.OUTSIDE_UP).getElevator() == elevator){
            return true;
        }
        if (SystemData.get().getGlobalOrder(floor, Button.OUTSIDE_DOWN) != null && elevator.getDirection() == Direction.DOWN && SystemData.get().getGlobalOrder(floor, Button.OUTSIDE_DOWN).getElevator() == elevator){
            return true;
        }
        return false;
    }

    public static void reassignElevatorOrders(Elevator elevator){
        SystemData data = SystemData.get();
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                if (data.getGlobalOrder(floor, button) != null && data.getGlobalOrder(floor, button).getElevator() == elevator){
                    Order tempOrder = data.getGlobalOrder(floor, button);
                    data.clearGlobalOrder(floor, button);
                    data.addGlobalOrder(tempOrder);
                    //Reassign order
                }
            }
        }
    }

    public Order getNextOrder(Elevator elevator){ //TODO: Check.
        SystemData data = SystemData.get();
        Direction direction = elevator.getDirection();
        if (direction == Direction.NONE){
            direction = Direction.UP;
        }
        if (direction == Direction.UP){
            List<Floor> reverseValues = Arrays.asList(Floor.values());
            Collections.reverse(reverseValues);
            for (Floor floor : reverseValues){
                if (elevator.getInternalOrder(floor) != null){
                    return elevator.getInternalOrder(floor);
                }
                else if(data.getGlobalOrder(floor, Button.OUTSIDE_DOWN) != null && data.getGlobalOrder(floor, Button.OUTSIDE_DOWN).getElevator() == elevator){
                    return data.getGlobalOrder(floor, Button.OUTSIDE_DOWN);
                }
                else if(data.getGlobalOrder(floor, Button.OUTSIDE_UP) != null && data.getGlobalOrder(floor, Button.OUTSIDE_UP).getElevator() == elevator){
                    return data.getGlobalOrder(floor, Button.OUTSIDE_UP);
                }
            }
        }
        else{
            for (Floor floor : Floor.values()){
                if (elevator.getInternalOrder(floor) != null){
                    return elevator.getInternalOrder(floor);
                }
                else if(data.getGlobalOrder(floor, Button.OUTSIDE_DOWN) != null && data.getGlobalOrder(floor,Button.OUTSIDE_DOWN).getElevator() == elevator){
                    return data.getGlobalOrder(floor, Button.OUTSIDE_DOWN);
                }
                else if(data.getGlobalOrder(floor, Button.OUTSIDE_UP) != null && data.getGlobalOrder(floor, Button.OUTSIDE_UP).getElevator() == elevator){
                    return data.getGlobalOrder(floor, Button.OUTSIDE_UP);
                }
            }
        }
        return null;
    }
    public int getNumberOfInternalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            if (elevator.getInternalOrder(floor) != null){
                orders++;
            }
        }
        return orders;
    }
}
