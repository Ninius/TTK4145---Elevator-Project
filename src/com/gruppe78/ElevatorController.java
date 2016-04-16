package com.gruppe78;

import com.gruppe78.driver.DriverController;
import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ElevatorPositionListener, OrderListener {
    private static ElevatorController sElevatorController;
    private volatile AtomicBoolean timer = new AtomicBoolean(false);
    private Elevator elevator;

    private ElevatorController(){}
    public static ElevatorController get(){
        if (sElevatorController == null){
            sElevatorController = new ElevatorController();
        }
        return sElevatorController;
    }

    public void init(){
        elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorMovementListener(this);
        elevator.addOrderEventListener(this);
        SystemData.get().addOrderEventListener(this);
    }

    public void startTimer(int time){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(this, "Timer started");
                    timer.set(true);
                    elevator.setDoor(true);
                    Thread.sleep(time);
                    timer.set(false);
                    elevator.setDoor(false);
                    Log.d(this, "Timer finished");
                    moveElevator();
                } catch (InterruptedException ex) {
                    Log.e(getClass().getName(), ex);
                }
            }
        });
        thread.setName("DoorTimer");
        thread.start();
    }

    public void moveElevator(){//TODO: Refactor? Double identical null checks.
        if (!timer.get() && elevator.getMotorDirection() == Direction.NONE){
            Log.d(this, "Moving Elevator");
            Order nextOrder = OrderHandler.getNextOrder(elevator);
            Log.d(this, "Order" + nextOrder);
            if (nextOrder == null) {
                Log.d(this, "Null order detected");
                return;
            }
            Log.d(this, "Order floor" + nextOrder.getFloor());
            Direction orderDirection = elevator.getFloor().directionTo(nextOrder.getFloor());
            Log.d(this, "New order direction: "+orderDirection);
            elevator.setMotorDirection(orderDirection);
            elevator.setOrderDirection(nextOrder.getButton().getDirection());
            if (orderDirection== Direction.NONE){
                startTimer(3000);
            }
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        //Temp
        Order nextOrder = OrderHandler.getNextOrder(elevator);
        if (nextOrder == null) return;
        if (nextOrder.getFloor() == newFloor || OrderHandler.isOrderOnFloor(newFloor, elevator) || newFloor.index == Floor.NUMBER_OF_FLOORS-1 || newFloor == Floor.FLOOR0){
            elevator.setMotorDirection(Direction.NONE);
            startTimer(3*1000);
            Log.d(this, "Elevator stopped on floor" + newFloor);
        }
        else {
            Log.d(this, "Order not on floor");
        }
    }

    @Override
    public void onOrderAdded(Order order){moveElevator();}
}
