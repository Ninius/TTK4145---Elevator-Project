package com.gruppe78.model;

/**
 * Created by jespe on 12.04.2016.
 */
public interface OrderEventListener {
    void onOrderAdded(Order order);
    void onOrderRemoved(Order order);
}
