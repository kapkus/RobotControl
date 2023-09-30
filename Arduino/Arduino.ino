#include "wmtRobot.h"
#include "BTManager.h"
#include <SoftwareSerial.h>	

SoftwareSerial ss(0, 4);
BTManager btManager = BTManager(&ss);

void setup() {
    Serial.begin(9600);
	btManager.begin(9600);
    Serial.println("Arduino ready");
}

void loop() {
    btManager.recvData();
  	if(btManager.newData){
  		btManager.parseData();
  		switch (btManager.lastCmd) {
  			case 'h':
  				Serial.println("x: " + String(btManager.getX()));
  				Serial.println("y: " + String(btManager.getY()));
  				break;
  			case 'p':
  				Serial.println("z: " + String(btManager.getZ()));
  				break;
  		}
  		btManager.sendConfirm();
  	}
}
