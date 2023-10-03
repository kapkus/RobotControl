## RobotControl ##
This project was a part of my diploma work which consisted of two parts:
* Android application created in Android Studio
* Arduino server-like end which listens for incoming input

![app control panel](images/app_control_panel.png)

### How it works ###
After installation of the app go to Bluetooth tab where you can connect phone with Arduino board (before establishing the connection you have to bind phone with board). If connection is successful you should be able to change current coordinates by moving joystick buttons around.

![app control panel input](images/app_control_panel_input.png)

These actions trigger sending currently moved axes (X and Y or Z) to the Arduino board with HC-05 module. The board then parses incoming data, updates its properties and sends back confirmation that signals the move has been done. Then robot can continue listening to new data.

![arduino result](images/arduino_result.png)

The application itself includes helpful functionalities which make robot control easier, such as:
* editable list of steps
* sequences manager
* home reset button

![app control sequence manager](images/app_control_sequence_manager.png)
