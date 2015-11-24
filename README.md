# planet-browser

A browser for random generated planets.

This application is intended as a showcase and testing environment for the rendering of random generated planets.

The same rendering algorithms will be used in the game InfiniteSpace.

## Setup Eclipse Development

Prerequisites:
1. Install Eclipse
2. Install Android Studio
3. Set environment variable ANDROID_HOME to the path of the Android SDK

In Eclipse or command line: 
1. Clone the repository

In commandline:
1. Go to the working directory of the git repository
2. `./gradlew`
3. `./gradlew :core:eclipse`
4. `./gradlew :desktop:eclipse`
5. `./gradlew :android:eclipse`
6. `./gradlew :html:eclipse`
7. `./gradlew :ios:eclipse`

In Eclipse:
1. Import projects from working directory of the git repository
2. Add assets directory as source directory (if necessary)