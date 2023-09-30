#include "BTManager.h"
#include "Arduino.h"
#include <SoftwareSerial.h>

BTManager::BTManager(SoftwareSerial *ss) : SoftwareSerial(0, 1){
	this->x = 0;
	this->y = 0;
	this->z = 0;
	this->newData = false;
	this->serial = ss;
}

void BTManager::begin(uint32_t baud){
	serial->begin(baud);
}

void BTManager::recvData(){
	static boolean recvInProgress = false;
	static byte ndx = 0;
	char startMarker = '<';
	char endMarker = '>';
	char rc;

	while (serial->available() > 0 && this->newData == false) {
		rc = serial->read();
		if (recvInProgress == true) {
			if (rc != endMarker) {
				this->receivedChars[ndx] = rc;
				ndx++;
				if (ndx >= this->numChars) {
					ndx = this->numChars - 1;
				}
			}
			else {
				this->receivedChars[ndx] = '\0';
				recvInProgress = false;
				ndx = 0;
				this->newData = true;
			}
		}else if (rc == startMarker) {
			recvInProgress = true;
		}
	}
}

void BTManager::parseData(){

	char *str = this->receivedChars;
	this->lastCmd = str[1];
	
	memmove(str, str + 2, (sizeof(receivedChars) - 2) / sizeof(str[0]));

	switch (this->lastCmd) {
		case 'h':
			charStringToFloat(str, &this->x, &this->y);
			break;
		case 'p':
			charStringToFloat(str, &this->z);
			break;
		default:
			Serial.println("Nieznana komenda");
			this->newData = false;
			break;
	}
}

void BTManager::charStringToFloat(char *str, float *x, float *y){
	char *p = strtok (str, ",");
	
	float floatValues[2];
	int i = 0;
	while (p != NULL)
	{
		floatValues[i++] = atof(p);
		p = strtok (NULL, ",");
	}

	*x = floatValues[0];
	*y = floatValues[1];
}

void BTManager::charStringToFloat(char *str, float *z){
	*z = atof(str);
}

void BTManager::sendConfirm(bool success = true){
	if(!success){
		Serial.print("Nie udalo sie wykonac ruchu ramieniem");
	}

	serial->write(this->lastCmd);

	this->newData = false;
}

// Setters and getters
void BTManager::setX(float newValue){
	this->x = newValue;
}
  
void BTManager::setY(float newValue){
	this->y = newValue;
}
  
void BTManager::setZ(float newValue){
	this->z = newValue;
}

float BTManager::getX(){
	return this->x;
}

float BTManager::getY(){
	return this->y;
}

float BTManager::getZ(){
	return this->z;
}
