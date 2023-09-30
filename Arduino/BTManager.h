#ifndef BTManager_h
#define BTManager_h

#include "Arduino.h"
#include <SoftwareSerial.h>

#define ARRAY_SIZE 64

class BTManager : public SoftwareSerial{
  	private:
		float x, y, z;
		const byte numChars = 64;
    	SoftwareSerial *serial;


		void setX(float newValue);
		void setY(float newValue);
		void setZ(float newValue);
    	void charStringToFloat(char *str, float *x, float *y);
		void charStringToFloat(char *str, float *z);
	public:
		bool newData;
		char lastCmd;
		BTManager(SoftwareSerial *ss);
		void begin(uint32_t baud);
		void parseData();
		void sendConfirm(bool success = true);
		void recvData();
		float getX();
		float getY();
		float getZ();
		char receivedChars[ARRAY_SIZE];
};


#endif
