package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ElevatorEventListener {
    private static ElevatorController sElevatorController;
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
            DriverHandler.setDoorOpenLamp(false);
            Order order = SystemData.get().getNextOrder(elevator);
            Direction orderDirection = elevator.getFloor().directionTo(order.getFloor());
            DriverHandler.setMotorDirection(orderDirection);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null){
            DriverHandler.setFloorIndicator(newFloor);
        }
        //Temp
        if (SystemData.get().getNextOrder(elevator).getFloor() == newFloor || SystemData.get().isOrderOnFloor(newFloor, elevator)){
            DriverHandler.setMotorDirection(Direction.NONE);
            DriverHandler.setDoorOpenLamp(true);
            SystemData.get().getLocalElevator().clearInternalOrder(newFloor);
            SystemData.get().clearGlobalOrder(newFloor);
            for(Button button : Button.values()){
                DriverHandler.setButtonLamp(button, newFloor, false);
            }
            startTimer(3*1000);
            elevator.clearInternalOrder(newFloor);
            SystemData.get().clearGlobalOrder(newFloor);
        }
        moveElevator();

    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {
        DriverHandler.setButtonLamp(button, floor, true);
        moveElevator();
    }
}
