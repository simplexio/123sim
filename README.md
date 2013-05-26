Group19 Home Control 26.5.2013
================================
Janna Honkavuori, 2265416, honkavuori.janna@gmail.com Janika Pasma, 2256658,
jpasma@ymail.com
GENERAL USAGE NOTES
==============================
This application is a remote control for Home automation system.
In the program you can see few things:
Device list: In the main page should appear in a different actuators and sensors, but this in this program it's blank page.
Settings menu: Press the emulator/device Menu button and you see the settings
button. By clicking this button you can see the server settings view. There you
can configure unit connections. Clicking checkbox you can set it to connect to
the specified server immediately after launch (checked) or from the menu manually
(unchecked). By editing the text box you can enter the central unit address.
After you have turned off the program and returned to the emulator/device main
menu you receive a notification after 20 seconds.
Note: Program does not work properly and it crashes in may situations.
RUNNING IN ECLIPSE EMULATOR
===============================
After you have imported the program to eclipse you can run the program in eclipse
emulator: 1. Press Run button (you need android virtual devices which API level
is 14) 2. Unlock the screen 3. It should directly open in emulator. If you are
using real device/phone it should be founded in Apps.
WORK DIVISION BETWEEN GROUP MEMBERS
====================================
List views, Intents, Preferences and Notifications: Janna & Janika
HomeControlService and model: Janna
JSON parsing: Janika
Networking and permissions: Janna made almost the entire task, Janika some small
pieces
Readme file, Testing & Profiling: Janika
Debugging and debugging messages: Janna & Janika
TESTING
========
-"The available sensors and actuators must be represented in a list, in a map and/or in other structure..."
Biggest problem in this our program is that you can't see nothing when you start the program. Sensors and actuators can't be seen in front page so you can't know states of the sensor and actuators and you cant't change of the states.
-"The application may be connected to at least one central unit at the same time"
This works because in the logcat view the connection appeared.
- "When the application is in the background, a notification on sensor value changes must be shown using the default notification mechanism of the platform."
This won't appear anywhere.
- "There must be a user interface to change the address and port of the central unit."
This works and it can be notice when push menu button and select "settings". By default, the program does not automatically connect the specidied server immediately after launch. In Settings, if you choose check box checked and try to go menu or back program fails. When you try to run the program again it craches and the program is fully unable to function.
In menu there are also Server Connect, Server Disconnect, Server Force Close, Server Refresh.
Server Connect:
Program crash and you have to re-open program.
Server Disconnect:
In logcat there is information that:
Disconnect from control unit, Check if there is a session, no session, stopping protocol... Protocol stopped, Model got error msg from server: No session. In the screen come notification that: Error from server: no session.
Server Force Close:
In logcat: Stopping protocol... Protocol stopped, Model got error msg from server: No session.
Server Refresh:
In logcat window: Refreshing device data from server, Model got error msg from server: No session. Also same notification appears to screen than before.
Like we can see, there is lots of problems.
PROFILING
=========
"Profiling must be done using a real device."
We tried profiling our application, without success. We discovered too late that the phone (samsung xcover) Api level was 10, not 14.
