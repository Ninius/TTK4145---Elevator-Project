package com.gruppe78.model;

/**
 * Created by jespe on 12.04.2016.
 */
public interface OrderListener {
    void onOrderAdded(Order order);
    void onOrderRemoved(Order order);
}
