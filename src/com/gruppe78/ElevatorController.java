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
    private final Elevator elevator;

    private ElevatorController(){
        elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorMovementListener(this);
        elevator.addOrderEventListener(this);
        SystemData.get().addOrderEventListener(this);
    }

    public void init(){
        Log.i(this, "Initializing");
        DriverController.get().onMotorDirectionChanged(Direction.DOWN);
        try{
            while (DriverHelper.getElevatorFloor() != Floor.FLOOR0){
                Thread.sleep(50);
            }
        }
        catch(Exception e){

        }

        DriverController.get().onMotorDirectionChanged(Direction.NONE);
        elevator.setFloor(Floor.FLOOR0);

        Log.i(this, "Initializing Done");
    }

    public static ElevatorController get(){
        if (sElevatorController == null){
            sElevatorController = new ElevatorController();
        }
        return sElevatorController;
    }


    public void startTimer(int time){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(this, "Timer started");
                    timer.set(true);
                    elevator.setDoor(true);
                    Thread.sleep(time);
                    timer.set(false);
                    elevator.setDoor(false);
                    Log.i(this, "Timer finished");
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
        if (!timer.get()){
            Log.i(this, "Moving Elevator");
            Order order = OrderHandler.getNextOrder(elevator);
            if (order == null) {
                Log.i(this, "Null order detected");
                return;
            }
            Log.i(this, "Order floor" + order.getFloor());
            Direction orderDirection = elevator.getFloor().directionTo(order.getFloor());
            Log.i(this, "New order direction: "+orderDirection);
            elevator.setMotorDirection(orderDirection);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        //Temp
        Order nextOrder = OrderHandler.getNextOrder(elevator);
        if (nextOrder == null) return;
        if (nextOrder.getFloor() == newFloor || OrderHandler.isOrderOnFloor(newFloor, elevator)){
            elevator.setMotorDirection(Direction.NONE);
            startTimer(3*1000);
            Log.i(this, "Order on floor");
        }
        else {
            Log.i(this, "Order not on floor");
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
