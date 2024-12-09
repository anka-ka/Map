# Journey Tracker
## Project description
This application is developed for people who are planning a trip and want to make sure they don’t miss anything.
For example, you’re planning a trip to another city and have already come up with many ideas for how to spend your time in various places. How do you ensure you won’t forget any of these exciting spots? Of course, by planning ahead! This app allows users to mark points of interest on the map and add personalized descriptions to them. Later, when the travelers arrive at their destination, they can open a list of places they’ve planned to visit and see where they are located on the map. That’s it! A perfect vacation without missing a single detail awaits you!

It allows users to:
1. View your location on a map.
2. Increase and decrease zoom on the map.
3. Click on the map to make a mark on the map and write a personalized description of it.
4. Long click on the check mark to see the description written by the user.
5. View a list of marked locations in a separate screen.
6. On clicking on the description in the list go to the marked place on the map.
7. Edit the description of the mark in the list and delete the mark.
8. Change app settings: the app supports Russian and English languages, dark and light theme.

  ## Demonstration of application functionality

![App functionality](assets/map_animation.gif)


## Technologies
1. Kotlin – programming language.
2. Jetpack Components:
* Hilt – for dependency injection.
* Room – for working with a local database.
* Navigation Component – for managing navigation between screens.
* ViewModel and LiveData – for state and data flow management.
3. Material Design – for styling the user interface.
4. Yandex MapKit – for working with map data.
5. Flow – for handling asynchronous data streams in real time.
6. SharedPreferences – for saving user settings.

## Installation and launch 
### Requirements: 
* Android 7.0 and above 
* Free space: 150 MB
  
### Installation:
* Download the APK file from the release.
* Install the APK on your device.

### Launch from source:
* Clone repository:git clone https://github.com/anka-ka/Map.git
* Open the project in Android Studio.
* Synchronize Gradle and run the app.
