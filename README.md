# planet-browser

A browser for random generated planets.

This application is intended as a showcase and testing environment for the rendering of random generated planets.

The same rendering algorithms will be used in the game InfiniteSpace.

![Example](core/docu/images/Earth1.png "Earth-like planet") 
![Example](core/docu/images/Earth2.png "Earth-like planet") 
![Example](core/docu/images/Earth3.png "Earth-like planet") 
![Example](core/docu/images/Earth4.png "Earth-like planet") 
![Example](core/docu/images/Lava1.png "Lava planet") 
![Example](core/docu/images/Lava2.png "Lava planet") 
![Example](core/docu/images/Lava3.png "Lava planet") 
![Example](core/docu/images/Moon1.png "Moon-like planet") 
![Example](core/docu/images/Jupiter1.png "Jupiter-like planet") 
![Example](core/docu/images/Jupiter2.png "Jupiter-like planet") 

## Setup Eclipse Development

Prerequisites:
* Install Eclipse
* Install Android Studio
* Set environment variable ANDROID_HOME to the path of the Android SDK

In Eclipse or command line: 
* Clone the repository

In command line:
* Go to the working directory of the git repository
* `./gradlew`
* `./gradlew eclipse`

In Eclipse:
* Import projects from working directory of the git repository
* Add `assets` directory as source directory (if necessary)
* Locate class `DesktopLauncher` and execute context menu: `Run as...` / `Java Application`