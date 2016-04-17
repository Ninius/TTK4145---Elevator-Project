package com.gruppe78;

import com.gruppe78.model.Floor;
import com.gruppe78.model.Order;
import com.gruppe78.model.SystemData;
import com.gruppe78.network.NetworkOrder;

/**
 * Created by oysteikh on 4/13/16.
 */
public class SerializedGlobalOrders implements java.io.Serializable {
    private NetworkOrder[][] globalOrders;
    public SerializedGlobalOrders(Order[][] orders){
        globalOrders = new NetworkOrder[Floor.NUMBER_OF_FLOORS][2];
        int i = 0; int j = 0;
        for (Order floor[] : orders){
            for (Order order : floor){
                if (order == null){
                    globalOrders[i][j] = null;
                }
                else{
                    globalOrders[i][j] = new NetworkOrder(order);
                }
                j++;
            }
            i++;
        }
    }
    public void updateGlobalOrders(SystemData data){
        Order newGlobalOrders[][] = new Order[Floor.NUMBER_OF_FLOORS][2];
        int i = 0; int j = 0;
        for (NetworkOrder[] floor : globalOrders){
            for (NetworkOrder order : floor){
                newGlobalOrders[i][j] = order.getOrder(data);
                j++;
            }
            i++;
        }
        data.setAllGlobalOrders(newGlobalOrders);
    }
}
