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
            openDoorAndClearOrders();
        }
    }

    @Override
    public void onDoorOpenChanged(boolean open){
        Log.d(this, "Door open changed:"+open);
        if(!open) moveElevator();
    }



    @Override
    public void onOrderAdded(Order order){
        Log.d(this, "OrderAdded");
        moveElevator();
    }

    public void openDoorAndClearOrders(){
        Log.d(this, "openDoorAndClearOrders() called");
        localElevator.setDoor(true);
        mDoorTimer.start();
        OrderHandler.clearOrder(localElevator.getFloor());
    }

    public void moveElevator(){
        Log.d(this,"MoveElevator called");
        if (localElevator.getMotorDirection() != Direction.NONE) return;

        Order nextOrder = OrderHandler.getNextOrder(localElevator);
        Log.d(this, "Next Order: "+nextOrder);
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
            Log.i(this, "count: "+count.get());
            if(count.decrementAndGet() == 0) onDoorTimerFinished();
        }
        boolean isRunning(){
            return count.get() == 0;
        }
        void start(){
            count.incrementAndGet();
            mThread.schedule(this,DOOR_OPEN_TIME, TimeUnit.MILLISECONDS);
        }
    }
}
