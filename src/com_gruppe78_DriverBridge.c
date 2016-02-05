#include "com_gruppe78_DriverBridge.h"
#include <comedilib.h>

static comedi_t *it_g = NULL;

#define PORT_1_SUBDEVICE 2
#define PORT_2_SUBDEVICE 3
#define PORT_3_SUBDEVICE 3
#define PORT_4_SUBDEVICE 3

#define PORT_1_CHANNEL_OFFSET 0
#define PORT_2_CHANNEL_OFFSET 0
#define PORT_3_CHANNEL_OFFSET 8
#define PORT_4_CHANNEL_OFFSET 16

#define PORT_1_DIRECTION COMEDI_INPUT
#define PORT_2_DIRECTION COMEDI_OUTPUT
#define PORT_3_DIRECTION COMEDI_OUTPUT
#define PORT_4_DIRECTION COMEDI_INPUT

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_init
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_gruppe78_DriverBridge_io_1init(JNIEnv * j,jclass class){
	it_g = comedi_open("/dev/comedi0");
	
	if (it_g == NULL) {
		printf("it_g was null");
		return JNI_FALSE;
	}
	int status = 0;
	int i;
	for (i = 0; i < 8; i++) {
		status |= comedi_dio_config(it_g, PORT_1_SUBDEVICE, i + PORT_1_CHANNEL_OFFSET, PORT_1_DIRECTION);
		status |= comedi_dio_config(it_g, PORT_2_SUBDEVICE, i + PORT_2_CHANNEL_OFFSET, PORT_2_DIRECTION);
		status |= comedi_dio_config(it_g, PORT_3_SUBDEVICE, i + PORT_3_CHANNEL_OFFSET, PORT_3_DIRECTION);
		status |= comedi_dio_config(it_g, PORT_4_SUBDEVICE, i + PORT_4_CHANNEL_OFFSET, PORT_4_DIRECTION);
	}

	if(status == 0){
		return JNI_TRUE;
	}else{
		return JNI_FALSE;
	}
}

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_set_bit
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_gruppe78_DriverBridge_io_1set_1bit
(JNIEnv * env, jclass class, jint jChannel){
	int ch = (int) jChannel;
	comedi_dio_write(it_g, ch >> 8, ch & 0xff, 1);
}

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_clear_bit
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_gruppe78_DriverBridge_io_1clear_1bit
(JNIEnv * env,jclass class, jint jChannel){
	int ch = (int) jChannel;
	comedi_dio_write(it_g, ch >> 8, ch & 0xff, 0);
}

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_write_analog
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_gruppe78_DriverBridge_io_1write_1analog
(JNIEnv * env, jclass class, jint jChannel, jint jValue){
	printf("Test write analog\n");
	int channel = (int) jChannel;
	int value = (int) jValue;
	printf("%i\n", channel);
	printf("%i\n", value);
	printf("%i\n", AREF_GROUND);
	comedi_data_write(it_g, channel >> 8, channel & 0xff, 0, AREF_GROUND, value);
}

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_read_bit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_gruppe78_DriverBridge_io_1read_1bit
(JNIEnv * env, jclass class, jint jChannel){
	int channel = (int) jChannel;
	unsigned int data = 0;
    comedi_dio_read(it_g, channel >> 8, channel & 0xff, &data);
    return (jint) ((int) data);
}

/*
 * Class:     com_gruppe78_DriverBridge
 * Method:    io_read_analog
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_gruppe78_DriverBridge_io_1read_1analog
(JNIEnv * env, jclass class, jint jChannel){
	int channel = (int) jChannel;
	lsampl_t data = 0;
    comedi_data_read(it_g, channel >> 8, channel & 0xff, 0, AREF_GROUND, &data);
    return (jint) ((int)data);
}
