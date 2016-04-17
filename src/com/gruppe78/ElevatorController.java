package com.gruppe78;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        localElevator.addElevatorPositionListener(this);
        localElevator.addOrderEventListener(this);
        systemData.addOrderEventListener(this);
    }

    @Override
    public void onFloorChanged(Floor newFloor) {
        Order nextOrder = OrderHandler.getNextOrder(localElevator);
        if (nextOrder == null) return;
        if (nextOrder.getFloor() == newFloor || OrderHandler.getMatchingOrder(localElevator, newFloor, localElevator.getMotorDirection()) != null || newFloor.isTop() || newFloor.isBottom()){
            localElevator.setMotorDirection(Direction.NONE);
            openDoorAndClearOrders();
        }
    }

    @Override
    public void onDoorOpenChanged(boolean open){
        if(!open) moveElevator();
    }



    @Override
    public void onOrderAdded(Order order){
        moveElevator();
    }

    public void openDoorAndClearOrders(){
        localElevator.setDoor(true);
        mDoorTimer.start();
        SystemData.get().clearAllOrders(localElevator.getFloor(), localElevator);
    }

    public void moveElevator(){
        if (localElevator.getMotorDirection() != Direction.NONE) return;

        Order nextOrder = OrderHandler.getNextOrder(localElevator);
        if (nextOrder == null) return;

        Direction orderDirection = localElevator.getFloor().directionTo(nextOrder.getFloor());

        if(orderDirection == Direction.NONE){
            openDoorAndClearOrders();
        }else if(!localElevator.isDoorOpen()){
            localElevator.setMotorDirection(orderDirection);
            localElevator.setOrderDirection(nextOrder.getButton().getDirection());
        }
    }

    private void onDoorTimerFinished(){
        localElevator.setDoor(false);
    }

    private class DoorTimer implements Runnable {
        private ScheduledExecutorService mThread = Executors.newSingleThreadScheduledExecutor();
        private AtomicInteger count = new AtomicInteger(0);
        @Override public void run() {
            if(count.decrementAndGet() == 0) onDoorTimerFinished();
        }
        boolean isRunning(){
            return count.get() == 0;
        } //Todo: Maybe remove.
        void start(){
            count.incrementAndGet();
            mThread.schedule(this,DOOR_OPEN_TIME, TimeUnit.MILLISECONDS);
        }
    }
}
