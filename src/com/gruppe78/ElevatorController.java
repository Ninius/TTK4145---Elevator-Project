package com.gruppe78;

import com.gruppe78.driver.DriverHelper;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController /*implements ElevatorPositionListener */{
    /*private static ElevatorController sElevatorController;
    private volatile boolean timer;
    private final Elevator elevator;

    private ElevatorController(){
        elevator = SystemData.get().getLocalElevator();
        elevator.addElevatorEventListener(this);
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
                    Thread.sleep(time);
                    timer = false;
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
            DriverHelper.setDoorOpenLamp(false);
            Order order = OrderHandler.getNextOrder(elevator);
            Direction orderDirection = elevator.getFloor().directionTo(order.getFloor());
            DriverHelper.setMotorDirection(orderDirection);
            OperativeManager.get().newEvent();
            OperativeManager.get().setActive(true);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        //Temp
        if (OrderHandler.getNextOrder(elevator).getFloor() == newFloor || OrderHandler.isOrderOnFloor(newFloor, elevator)){
            DriverHelper.setMotorDirection(Direction.NONE);
            DriverHelper.setDoorOpenLamp(true);
            SystemData.get().getLocalElevator().clearInternalOrder(newFloor);
            SystemData.get().clearGlobalOrder(newFloor);
            for(Button button : Button.values()){
                DriverHelper.setButtonLamp(button, newFloor, false);
            }
            startTimer(3*1000);
            elevator.clearInternalOrder(newFloor);
            SystemData.get().clearGlobalOrder(newFloor);
        }
        moveElevator();

    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection) {}

    @Override
    public void onOrderDirectionChanged(Direction newDirection) {}

    @Override
    public void onDoorOpenChanged(boolean newOpen) {
        //Nothing
    }
    */
}
