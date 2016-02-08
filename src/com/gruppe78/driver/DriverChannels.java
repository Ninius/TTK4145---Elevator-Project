package com.gruppe78.driver;

/**
 * Created by student on 05.02.16.
 */
public class DriverChannels {

    //In port 4:
    static final int OBSTRUCTION = (0x300+23);
    static final int STOP = (0x300+22);
    static final int BUTTON_COMMAND1 = (0x300+21);
    static final int BUTTON_COMMAND2 = (0x300+20);
    static final int BUTTON_COMMAND3 = (0x300+19);
    static final int BUTTON_COMMAND4 = (0x300+18);
    static final int BUTTON_UP1 = (0x300+17);
    static final int BUTTON_UP2 = (0x300+16);

    //In port 1:
    static final int BUTTON_DOWN2 = (0x200+0);
    static final int BUTTON_UP3 = (0x200+1);
    static final int BUTTON_DOWN3 = (0x200+2);
    static final int BUTTON_DOWN4 = (0x200+3);
    static final int SENSOR_FLOOR1 = (0x200+4);
    static final int SENSOR_FLOOR2 = (0x200+5);
    static final int SENSOR_FLOOR3 = (0x200+6);
    static final int SENSOR_FLOOR4 = (0x200+7);

    //Out port 3:
    static final int MOTORDIR = (0x300+15);
    static final int LIGHT_STOP = (0x300+14);
    static final int LIGHT_COMMAND1 = (0x300+13);
    static final int LIGHT_COMMAND2 = (0x300+12);
    static final int LIGHT_COMMAND3 = (0x300+11);
    static final int LIGHT_COMMAND4 = (0x300+10);
    static final int LIGHT_UP1 = (0x300+9);
    static final int LIGHT_UP2 = (0x300+8);

    //Out port 2:
    static final int LIGHT_DOWN2 = (0x300+7);
    static final int LIGHT_UP3 = (0x300+6);
    static final int LIGHT_DOWN3 = (0x300+5);
    static final int LIGHT_DOWN4 = (0x300+4);
    static final int LIGHT_DOOR_OPEN = (0x300+3);
    static final int LIGHT_FLOOR_IND2 = (0x300+1);
    static final int LIGHT_FLOOR_IND1 = (0x300+0);

    //out port 0
    static final int MOTOR = (0x100+0);

    //non-existing ports (for alignment)
    static final int BUTTON_DOWN1 = -1;
    static final int BUTTON_UP4 = -1;
    static final int LIGHT_DOWN1 = -1;
    static final int LIGHT_UP4 = -1;

    //Matrices for convenience:
    static final int[][] LAMP_CHANNEL_MATRIX = {
            {LIGHT_UP1, LIGHT_DOWN1, LIGHT_COMMAND1},
            {LIGHT_UP2, LIGHT_DOWN2, LIGHT_COMMAND2},
            {LIGHT_UP3, LIGHT_DOWN3, LIGHT_COMMAND3},
            {LIGHT_UP4, LIGHT_DOWN4, LIGHT_COMMAND4},
    };


    static final int[][] BUTTON_CHANNEL_MATRIX = {
            {BUTTON_UP1, BUTTON_DOWN1, BUTTON_COMMAND1},
            {BUTTON_UP2, BUTTON_DOWN2, BUTTON_COMMAND2},
            {BUTTON_UP3, BUTTON_DOWN3, BUTTON_COMMAND3},
            {BUTTON_UP4, BUTTON_DOWN4, BUTTON_COMMAND4},
    };
}
