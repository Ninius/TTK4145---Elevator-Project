package com.gruppe78;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls the localElevator based on changes in model.
 */
public class ElevatorController implements ElevatorPositionListener, OrderListener {
    private static ElevatorController sElevatorController;

    private Elevator localElevator;
    private SystemData systemData;

    private static final long DOOR_OPEN_TIME = 3000;
    private DoorTimer mDoorTimer = new DoorTimer();

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
        }else{
            moveElevator();
        }

    }

    @Override
    public void onOrderAdded(Order order){
        moveElevator();
    }

    public void moveElevator(){
        if (mDoorTimer.isRunning() || localElevator.getMotorDirection() != Direction.NONE) return;

        Order nextOrder = OrderHandler.getNextOrder(localElevator);
        if (nextOrder == null) return;

        Direction orderDirection = localElevator.getFloor().directionTo(nextOrder.getFloor());
        localElevator.setMotorDirection(orderDirection);
        localElevator.setOrderDirection(nextOrder.getButton().getDirection());

        if (orderDirection== Direction.NONE) mDoorTimer.start();
    }

    private void onDoorTimerFinished(){
        localElevator.setDoor(false);
    }

    private class DoorTimer implements Runnable {
        private ExecutorService mThread = Executors.newFixedThreadPool(1);
        private volatile boolean running = false;
        @Override public void run() {
            running = true;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
            onDoorTimerFinished();
        }
        boolean isRunning(){
            return running;
        }
        void start(){
            mThread.execute(this);
        }
    }
}
