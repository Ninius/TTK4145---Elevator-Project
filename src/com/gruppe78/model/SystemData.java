package com.gruppe78.model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Because SystemData is synchronized there should be as little interaction with it as possible.
 */
public class SystemData {
    private static SystemData sSystemData;
    private final List<Elevator> mElevators;
    private Order[][] globalOrders = new Order[Floor.NUMBER_OF_FLOORS][Button.NUMBER_OF_BUTTONS - 1];

    /****************************************************************************************************************
     * Constructing and obtaining of reference. Initialization of elevators must be done before.
     ****************************************************************************************************************/

    private SystemData(ArrayList<Elevator> elevators){
        mElevators = Collections.unmodifiableList(elevators);
    }

    public static void init(ArrayList<Elevator> elevators) {
        if (sSystemData != null) return;
        sSystemData = new SystemData(elevators);
    }

    public static SystemData get(){
        return sSystemData;
    }

    /***************************************************************************************************************
     * Elevators
     ***************************************************************************************************************/

    public List<Elevator> getElevatorList(){
        return mElevators;
    }

    public Elevator getLocalElevator(){
        for(Elevator elevator : mElevators){
            if(elevator.isLocal()) return elevator;
        }
        return null;
    }
    public Elevator getElevator(InetAddress inetAddress){
        for(Elevator elevator : mElevators){
            if(elevator.getInetAddress().equals(inetAddress)) return elevator;
        }
        return null;
    }

    /***************************************************************************************************************
     * Orders
     ***************************************************************************************************************/
    public synchronized Order getOrder(Floor floor, Button button){
        if(button == Button.INTERNAL) return null;
        return globalOrders[floor.index][button.index];
    }


    public int getNumberOfGlobalOrders(Elevator elevator){ //TODO: Necessary? Move?
        int orders = 0;
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                if(getOrder(floor,button) != null && getOrder(floor,button).getElevator() == elevator){
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
        if (getOrder(floor, Button.OUTSIDE_UP) != null && elevator.getDirection() == Direction.UP && getOrder(floor, Button.OUTSIDE_UP).getElevator() == elevator){
            return true;
        }
        if (getOrder(floor, Button.OUTSIDE_DOWN) != null && getLocalElevator().getDirection() == Direction.DOWN && getOrder(floor, Button.OUTSIDE_DOWN).getElevator() == getLocalElevator()){
            return true;
        }
        return false;
    }
    public void addGlobalOrder(Order order){
        if(order.getType() == Button.INTERNAL) return;
        if(globalOrders[order.getFloor().index][order.getType().index] != null) return;

        globalOrders[order.getFloor().index][order.getType().index] = order;
    }
    public void clearGlobalOrder(Floor floor){
        globalOrders[floor.index][0] = null;
        globalOrders[floor.index][1] = null;
    }

    public void reassignElevatorOrders(Elevator elevator){ //TODO: Move
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

    public Order getNextOrder(Elevator elevator){ //TODO: Check.
        Direction direction = elevator.getDirection();
        if (direction == Direction.NONE){
            direction = Direction.UP;
        }
        if (direction == Direction.UP){
            for (Floor floor : Floor.values()){
                if (getLocalElevator().getInternalOrder(floor) != null){
                    return getLocalElevator().getInternalOrder(floor);
                }
                else if(getOrder(floor, Button.OUTSIDE_DOWN) != null && getOrder(floor,Button.OUTSIDE_DOWN).getElevator() == getLocalElevator()){
                    return getOrder(floor, Button.OUTSIDE_DOWN);
                }
                else if(getOrder(floor, Button.OUTSIDE_UP) != null && getOrder(floor, Button.OUTSIDE_UP).getElevator() == getLocalElevator()){
                    return getOrder(floor, Button.OUTSIDE_UP);
                }
            }
        }
        else{
            for (Floor floor : Floor.values()){
                if (getLocalElevator().getInternalOrder(floor) != null){
                    return getLocalElevator().getInternalOrder(floor);
                }
                else if(getOrder(floor, Button.OUTSIDE_DOWN) != null && getOrder(floor,Button.OUTSIDE_DOWN).getElevator() == getLocalElevator()){
                    return getOrder(floor, Button.OUTSIDE_DOWN);
                }
                else if(getOrder(floor, Button.OUTSIDE_UP) != null && getOrder(floor, Button.OUTSIDE_UP).getElevator() == getLocalElevator()){
                    return getOrder(floor, Button.OUTSIDE_UP);
                }
            }
        }
        return null;
    }
}
