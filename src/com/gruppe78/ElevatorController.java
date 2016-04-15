package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ElevatorPositionListener, OrderListener {
    private static ElevatorController sElevatorController;
    private volatile boolean timer;
    private final Elevator elevator;

    private ElevatorController(){
        elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorMovementListener(this);
        elevator.addOrderEventListener(this);
    }

    public static void init(){
        if(sElevatorController != null) return;
        sElevatorController = new ElevatorController();
    }

    public static ElevatorController get(){
        return sElevatorController;
    }


    public void startTimer(int time){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timer = true;
                    elevator.setDoor(true);
                    Thread.sleep(time);
                    timer = false;
                    elevator.setDoor(false);
                    moveElevator();
                } catch (InterruptedException ex) {
                    Log.e(getClass().getName(), ex);
                }
            }
        });
        thread.setName("DoorTimer");
        thread.start();
    }

    public void moveElevator(){
        if (!timer){
            Order order = OrderHandler.getNextOrder(elevator);
            Direction orderDirection = elevator.getFloor().directionTo(order.getFloor());
            elevator.setMotorDirection(orderDirection);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        //Temp
        if (OrderHandler.getNextOrder(elevator).getFloor() == newFloor || OrderHandler.isOrderOnFloor(newFloor, elevator)){
            elevator.setMotorDirection(Direction.NONE);
            startTimer(3*1000);
        }
    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection) {}

    @Override
    public void onOrderDirectionChanged(Direction newDirection) {}

    @Override
    public void onDoorOpenChanged(boolean newOpen) {
        //Nothing
    }
    @Override
    public void onOrderAdded(Order order){moveElevator();}
    public void onOrderRemoved(Order order){};


}
