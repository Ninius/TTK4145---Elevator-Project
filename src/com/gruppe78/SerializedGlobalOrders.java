package com.gruppe78;

import com.gruppe78.model.Floor;
import com.gruppe78.model.Order;
import com.gruppe78.model.SystemData;

/**
 * Created by oysteikh on 4/13/16.
 */
public class SerializedGlobalOrders implements java.io.Serializable {
    private SerializedOrder[][] globalOrders;
    public SerializedGlobalOrders(Order[][] orders){
        globalOrders = new SerializedOrder[Floor.NUMBER_OF_FLOORS][2];
        int i = 0; int j = 0;
        for (Order floor[] : orders){
            for (Order order : floor){
                if (order == null){
                    globalOrders[i][j] = null;
                }
                else{
                    globalOrders[i][j] = new SerializedOrder(order, false);
                }
                j++;
            }
            i++;
        }
    }
    public void updateGlobalOrders(){
        Order newGlobalOrders[][] = new Order[Floor.NUMBER_OF_FLOORS][2];
        int i = 0; int j = 0;
        for (SerializedOrder[] floor : globalOrders){
            for (SerializedOrder order : floor){
                newGlobalOrders[i][j] = order.getOrder();
                j++;
            }
            i++;
        }
        SystemData.get().setAllGlobalOrders(newGlobalOrders);
    }
}
