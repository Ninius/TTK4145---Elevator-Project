package com.gruppe78;

import com.gruppe78.model.SystemData;

import java.net.InetAddress;

/**
 * Created by oysteikh on 4/13/16.
 */

//First draft, please change if needed.
public class SerializedOrderCost implements java.io.Serializable {
    private InetAddress mSender;
    private SerializedOrder mSerializedOrder;
    private Integer mCost; //Null when requesting cost, value when sending cost?

    public SerializedOrderCost(InetAddress sender, SerializedOrder serializedOrder, int cost){
        mSerializedOrder = serializedOrder;
        mCost = cost;
        mSender = sender;
    }
    public int getCost(){
        return mCost;
    }
    public SerializedOrder getSerializedOrder(){
        return mSerializedOrder;
    }
    public InetAddress getSender(){
        return mSender;
    }
    public void updateCost(){
        mCost = CostFunction.costFunction(mSerializedOrder.getOrder());
        mSender = SystemData.get().getLocalElevator().getInetAddress();
    }
}
