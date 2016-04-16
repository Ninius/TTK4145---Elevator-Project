package com.gruppe78;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls the localElevator based on changes in model.
 */
public class ElevatorController implements ElevatorPositionListener, OrderListener {
    private static ElevatorController sElevatorController;
    private static final int DOOR_OPEN_TIME = 3000;

    private volatile AtomicBoolean timer = new AtomicBoolean(false);
    private Elevator localElevator;
    private DoorTimer mDoorTimer;
    private SystemData systemData;

    private ElevatorController(){}
    public static ElevatorController get(){
        if (sElevatorController == null){
            sElevatorController = new ElevatorController();
        }
        return sElevatorController;
    }

    public void init(){
        systemData = SystemData.get();
        localElevator = systemData.getLocalElevator();
        localElevator.addElevatorMovementListener(this);
        localElevator.addOrderEventListener(this);
        systemData.addOrderEventListener(this);
        mDoorTimer = new DoorTimer();
        mDoorTimer.setName("DoorTimer");
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        Order nextOrder = OrderHandler.getNextOrder(localElevator);
        if (nextOrder == null) return;
        if (nextOrder.getFloor() == newFloor || OrderHandler.isOrderOnFloor(newFloor, localElevator) || newFloor.isTop() || newFloor.isBottom()){
            localElevator.setMotorDirection(Direction.NONE);
            localElevator.setDoor(true);
        }
    }

    @Override
    public void onMotorDirectionChanged(Direction newDirection){
        if (newDirection == Direction.NONE) OrderHandler.clearOrder(systemData.getLocalElevator().getFloor());
    };

    @Override
    public void onDoorOpenChanged(boolean open){
        if(open){
            mDoorTimer.start();
        }else if(!mDoorTimer.isRunning() && localElevator.getMotorDirection() == Direction.NONE) {
            Order nextOrder = OrderHandler.getNextOrder(localElevator);
            Log.d(this, "Next order: " + nextOrder);
            if (nextOrder == null) return;

            Direction orderDirection = localElevator.getFloor().directionTo(nextOrder.getFloor());
            Log.d(this, "New order direction: "+orderDirection);
            localElevator.setMotorDirection(orderDirection);
            localElevator.setOrderDirection(nextOrder.getButton().getDirection());

            if (orderDirection== Direction.NONE) mDoorTimer.start();
        }

    }

    private void onDoorTimerFinished(){
        localElevator.setDoor(false);
    }

    private class DoorTimer extends Thread{
        private AtomicBoolean running = new AtomicBoolean(false);
        @Override public void run() {
            try {
                running.set(true);
                Thread.sleep(DOOR_OPEN_TIME);
                running.set(false);
                onDoorTimerFinished();
            } catch (InterruptedException ex) {
                running.set(false);
                onDoorTimerFinished();
                Log.e(this, ex);
            }
        }
        public boolean isRunning(){
            return running.get();
        }
    }
}
