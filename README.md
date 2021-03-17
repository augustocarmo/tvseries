TV Series
=====

Project done by Augusto Carmo to accomplish a Jobsity's Challenge

Considerations
--------

As my personal computer is not powerful enough to run a virtual device, I couldn't manage to test on other Android apis besides my personal Android device.

My device is: Motorola G5 plus - Android 8.1.0

Setup
--------

To setup the project, clone it to your computer and run the application module (:app)


Installation
-------

You can find the apk on:
* distribution folder;
* Github Releases

Details
--------

Credits to TVMAZE for the [free api](https://www.tvmaze.com/api). That api is used all along the project.

This project uses or applies:
* Coroutines
* Navigation Component
  * SafeArgs
* MVVM architecture
  * LiveData
  * ViewModel
* Dependency Injection
  * Koin
* Retrofit
* Glide
* Modularization
  * app (the application module)
  * core
  * repository
  * viewmodels
  * webservice
* Material Components

Challenge Features
--------

**Mandatory**

* :heavy_check_mark: List all of the series contained in the API used by the paging scheme provided by the API.
* :heavy_check_mark: Allow users to search series by name.
* :heavy_check_mark: The listing and search views must show at least the name and poster image of the series.
* :heavy_check_mark: After clicking on a series, the application should show the details of the series, showing the following information:
  * Name
  * Poster
  * Days and time during which the series airs
  * Genres
  * Summary
  * List of episodes separated by season

* :heavy_check_mark: After clicking on an episode, the application should show the episode’s information, including:
  * Name
  * Number
  * Season
  * Summary
  * Image, if there is one
  
  
**Bonus (Optional)**
* Allow the user to set a PIN number to secure the application and prevent unauthorized users.
* For supported phones, the user must be able to choose if they want to enable fingerprint authentication to avoid typing the PIN number while opening the app.
* Allow the user to save a series as a favorite.
* Allow the user to delete a series from the favorites list.
* Allow the user to browse their favorite series in alphabetical order, and click on one to see its details.
* Create a people search by listing the name and image of the person.
* After clicking on a person, the application should show the details of that person, such as:
  * Name
  * Image
  * Series they have participated in, with a link to the series details.