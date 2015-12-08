# planet-browser

A browser for random generated planets.

This application is intended as a showcase and testing environment for the rendering of random generated planets.

The same rendering algorithms will be used in the game InfiniteSpace.


## Screenshot Gallery

![Earth-like planet](core/docu/images/Earth1.png "Earth-like planet") 
![Mountain range on Earth-like planet](core/docu/images/Earth2.png "Mountain range on Earth-like planet") 
![Sunset on Earth-like planet](core/docu/images/Earth3.png "Sunset on Earth-like planet") 
![Completely frozen Earth-like planet](core/docu/images/Earth4.png "Completely frozen Earth-like planet") 
![Mostly molten lava planet](core/docu/images/Lava1.png "Mostly molten lava planet") 
![Cooled down lava planet](core/docu/images/Lava2.png "Cooled down lava planet") 
![Cooling lava planet](core/docu/images/Lava3.png "Cooling lava planet") 
![Moon](core/docu/images/Moon1.png "Moon") 
![Moon](core/docu/images/Moon3.png "Moon") 
![Mars-like planet with atmosphere](core/docu/images/Mars1.png "Mars") 
![Mars-like planet with atmosphere](core/docu/images/Mars2.png "Mars") 
![Jupiter-like planet](core/docu/images/Jupiter1.png "Jupiter-like planet") 
![Jupiter-like planet](core/docu/images/Jupiter2.png "Jupiter-like planet") 

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
