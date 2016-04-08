package com.gruppe78;

import com.gruppe78.driver.DriverHandler;
import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.sql.Driver;

/**
 * Controls the elevator based on changes in model.
 */
public class ElevatorController implements ElevatorEventListener {
    private static ElevatorController sElevatorController;
    private Elevator localElevator;
    private volatile boolean timer;
    private ElevatorController(Elevator elevator){
        localElevator = elevator;
        localElevator.addElevatorEventListener(this);
    }

    public static void init(Elevator elevator){
        if(sElevatorController != null) return;
        sElevatorController = new ElevatorController(elevator);
    }

    public static ElevatorController get(){
        return sElevatorController;
    }

    public void Timer(int time){
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
    }
    public void moveElevator(){
        if (!timer){
            DriverHandler.setDoorOpenLamp(false);
            Order order = Model.get().getLocalElevator().getNextOrder();
            MotorDirection  orderDirection = MotorDirection.values()[order.getFloor().index - localElevator.getFloor().index];
            DriverHandler.setMotorDirection(orderDirection);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        if(newFloor != null){
            DriverHandler.setFloorIndicator(newFloor);
        }
        /*if (Model.get().getLocalElevator().getNextOrder().getFloor() == newFloor.index){
            DriverHandler.setMotorDirection(MotorDirection.STOP);
            DriverHandler.setDoorOpenLamp(true);
            timer(3*1000);
            Model.get().getLocalElevator().clearOrder(newFloor);

        }*/
        moveElevator();

    }

    @Override
    public void onButtonPressed(Floor floor, Button button, boolean newValue) {
        DriverHandler.setButtonLamp(button, floor, true);
        moveElevator();
    }
}
