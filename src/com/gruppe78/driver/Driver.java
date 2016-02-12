package com.gruppe78.driver;

/**
 * Created by student on 12.02.16.
 */
interface Driver {
    boolean io_init(); //Returns true if successful.
    void io_set_bit(int channel);
    void io_clear_bit(int channel);
    void io_write_analog(int channel, int value);
    int io_read_bit(int channel);
    int io_read_analog(int channel);
}
