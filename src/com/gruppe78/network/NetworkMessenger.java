package com.gruppe78.network;

import com.gruppe78.model.*;
import com.gruppe78.utilities.Log;

/**
 * Created by jespe on 14.04.2016.
 */
public class NetworkMessenger implements ElevatorPositionListener, ElevatorStatusListener, OrderListener{
    private static NetworkMessenger sNetworkMessager = new NetworkMessenger();
    private Elevator localElevator;
    private SystemData data;
    private NetworkStarter networkStarter;
    private Order lastOrderAddedReceived = null;
    private Order lastOrderRemovedReceived = null;

    private NetworkMessenger(){
        data = SystemData.get();

    }
    public static NetworkMessenger get(){
        return sNetworkMessager;
    }

    public void initBroadcasting(){
        localElevator = data.getLocalElevator();
        localElevator.addElevatorPositionListener(this);
        localElevator.addOrderEventListener(this);
        data.addOrderEventListener(this);
        networkStarter = NetworkStarter.get();

        for(Elevator elevator : SystemData.get().getElevatorList()){
            elevator.addElevatorStatusListener(this);
        }
    }

    void decodeMessage(NetworkMessage message, Elevator sender){
        Log.i(this, "Receiving msg from "+sender+":"+message);
        switch (message.getMessage()){
            case "FloorChanged":
                sender.setFloor((Floor) message.getData());
                return;
            case "MotorDirectionChanged":
                sender.setMotorDirection((Direction) message.getData());
                return;
            case "OrderDirectionChanged":
                sender.setOrderDirection((Direction) message.getData());
                return;
            case "DoorOpenChanged":
                sender.setDoor((Boolean) message.getData());
                return;
            case "OperableChanged":
                sender.setOperable((Boolean) message.getData());
                return;
            case "OrderAdded":
                NetworkOrder networkOrder = (NetworkOrder) message.getData();
                lastOrderAddedReceived = networkOrder.getOrder(data);
                data.addOrder(lastOrderAddedReceived);
                return;
            case "OrderRemoved":
                networkOrder = (NetworkOrder) message.getData();
                lastOrderRemovedReceived = networkOrder.getOrder(data);
                data.clearOrder(lastOrderRemovedReceived);
                return;
            default:
                Log.e(this, "Could not decode message.");
        }
    }

    synchronized void sendMessage(NetworkMessage message){ //TODO: Create thread.
        for(Elevator elevator : data.getElevatorList()){
            if(elevator == localElevator) continue;
            ElevatorConnection connection = networkStarter.getConnection(elevator);
            if(connection == null) continue;
            boolean success = connection.sendMessage(message);
            Log.i(this, "Sending msg to "+elevator+":"+message+". Success:"+success);
        }
    }

    @Override
    public void onFloorChanged(Floor newFloor){
        sendMessage(new NetworkMessage("FloorChanged", newFloor));
    }
    @Override
    public void onMotorDirectionChanged(Direction newDirection){
        sendMessage(new NetworkMessage("MotorDirectionChanged", newDirection));
    }
    @Override
    public void onOrderDirectionChanged(Direction newDirection){
        sendMessage(new NetworkMessage("OrderDirectionChanged", newDirection));
    }
    @Override
    public void onDoorOpenChanged(boolean newOpen){
        //sendMessage(new NetworkMessage("DoorOpenChanged", newOpen));
    }

    @Override
    public void onConnectionChanged(Elevator elevator, boolean connected) {
        if (elevator != SystemData.get().getLocalElevator() && connected){
            //Sending internal orders to the elevator:
            for (Order order : elevator.getAllInternalOrders()){
                if (order == null) continue;
                sendMessage(new NetworkMessage("OrderAdded", new NetworkOrder(order)));
            }

            //Sending info about local elevator.
            Elevator localElevator = SystemData.get().getLocalElevator();
            sendMessage(new NetworkMessage("FloorChanged", localElevator.getFloor()));
            sendMessage(new NetworkMessage("MotorDirectionChanged", localElevator.getMotorDirection()));
            sendMessage(new NetworkMessage("OperableChanged", localElevator.isOperable()));
            sendMessage(new NetworkMessage("DoorOpenChanged", localElevator.isDoorOpen()));
            for(Order[] orderList : SystemData.get().getAllGlobalOrders()){
                for(Order order : orderList){
                    if(order == null) continue;
                    sendMessage(new NetworkMessage("OrderAdded", new NetworkOrder(order)));
                }
            }
        }
    }

    @Override
    public void onOperableChanged(Elevator elevator, boolean operable) {
        if(elevator != SystemData.get().getLocalElevator()) return;
        sendMessage(new NetworkMessage("OperableChanged", operable));
    }

    @Override
    public void onOrderAdded(Order order){
        if(order == lastOrderAddedReceived) return;
        sendMessage(new NetworkMessage("OrderAdded", new NetworkOrder(order)));
    }

    @Override
    public void onOrderRemoved(Order order){
        if(order == lastOrderRemovedReceived) return;
        sendMessage(new NetworkMessage("OrderRemoved", new NetworkOrder(order)));
    }
}
