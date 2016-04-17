package com.gruppe78;

import com.gruppe78.model.*;

import java.util.List;

/**
 * Created by oysteikh on 4/11/16.
 */
public class OrderHandler implements ElevatorStatusListener, ElevatorPositionListener{
    private static OrderHandler mOrderHandler;
    private OrderHandler (){
        for (Elevator elevator : SystemData.get().getElevatorList()){
            elevator.addElevatorStatusListener(this);
            elevator.addElevatorPositionListener(this);
        }
    }
    public static OrderHandler get(){
        if (mOrderHandler == null){
            mOrderHandler = new OrderHandler();
        }
        return mOrderHandler;
    }

    public static void onButtonPressed(Button button, Floor floor){
        Elevator localElevator = SystemData.get().getLocalElevator();
        Elevator orderElevator = (button != Button.INTERNAL) ? CostFunction.getMinCostElevator(button, floor) : localElevator;
        SystemData.get().addOrder(new Order(orderElevator, button, floor));
    }

    @Override
    public void onConnectionChanged(Elevator elevator, boolean connected){
        if (!connected){
            List<Elevator> elevatorList = SystemData.get().getElevatorList();
            Elevator maxElevator = elevatorList.get(0);
            for (Elevator mElevator : elevatorList){
                if (mElevator.getID()>maxElevator.getID() && mElevator != elevator){
                    maxElevator = mElevator;
                }
            }
            if (maxElevator == SystemData.get().getLocalElevator()){
                reassignElevatorOrders(elevator);
            }
        }
    }
    @Override
    public void onOperableChanged(Elevator elevator, boolean operable){
        if (!operable && elevator == SystemData.get().getLocalElevator()){
            reassignElevatorOrders(SystemData.get().getLocalElevator());
        }
    }

    public static void reassignElevatorOrders(Elevator elevator){
        SystemData data = SystemData.get();

        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                Order order = data.getGlobalOrder(floor, button.isUp());
                if (order != null && order.getElevator() == elevator){
                    Elevator lowestCostElevator = CostFunction.getMinCostElevator(button, floor);
                    Order newOrder = new Order(lowestCostElevator, order.getButton(), order.getFloor());
                    data.clearOrder(order);
                    data.addOrder(newOrder);
                }
            }
        }
    }

    public static Order getNextOrder(Elevator elevator){ //TODO: Check
        Direction direction = elevator.getMotorDirection() != Direction.NONE ? elevator.getMotorDirection() : elevator.getOrderDirection();
        Floor floor = (direction == Direction.UP ? Floor.getTopFloor() : Floor.getBottomFloor());
        if (direction == Direction.UP || direction == Direction.NONE) direction = Direction.DOWN;
        else direction = Direction.UP;

        Order order;
        while(floor != null){
            order = getMatchingOrder(elevator, floor, Direction.NONE);
            if(order != null) return order;
            floor = floor.getNextFloor(direction);
        }
        return null;
    }

    public static Order getMatchingOrder(Elevator elevator, Floor floor, Direction orderDirection){
        SystemData data = SystemData.get();
        for(Button button : Button.values()){
            Order order = (button == Button.INTERNAL) ? elevator.getInternalOrder(floor) : data.getGlobalOrder(floor, button.isUp());
            if(order != null && order.getElevator() == elevator && button.matchDirection(orderDirection)) return order;
        }
        return null;
    }

    public static int getNumberOfInternalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            if (elevator.getInternalOrder(floor) != null) orders++;
        }
        return orders;
    }
    public static int getNumberOfGlobalOrders(Elevator elevator){
        int orders = 0;
        for (Floor floor : Floor.values()){
            for (Button button : Button.values()){
                if(button == Button.INTERNAL) continue;
                Order order = SystemData.get().getGlobalOrder(floor, button.isUp());
                if(order != null && order.getElevator() == elevator) orders++;
            }
        }
        return orders;
    }
}
