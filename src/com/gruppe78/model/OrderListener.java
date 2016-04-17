package com.gruppe78.model;

/**
 * Created by jespe on 12.04.2016.
 */
public interface OrderListener {
    default void onOrderAdded(Order order){}

    default void onOrderRemoved(Order order){}
}
