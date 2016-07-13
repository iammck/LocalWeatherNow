#Quick Temps

[Download the app from google play](https://play.google.com/store/apps/details?id=com.mck.quicktemps)

This project builds upon a [previous](https://github.com/iammck/WeatherNow) project. The main goal here is to expand upon the original using Android Studio 2.2 along with Constraints Layouts to produce a more robust user experience.

<font color='red'>In order to build and run this project you will need to get an OpenWeatherMaps API key and include it in the Constants class in the appropriate field. </font>

## Getting Data

The application uses [OpenWeatherMaps](http://openweathermap.org/) to request data over the network using the users current location. Getting the user location is handled by Google Play Services and the Weather data request is made using an HttpURLConnection. The resulting JSon string is parsed using Gson into custom weather data objects.

### Model

The model and tests come directly from the previous project. The incoming data is a JSon string and the model is composed so as to collect that data into usable POJOs. Rain and Snow objects are special. The incoming JSon string has invalid names for each item, thus they need to use custom deserializers. The deserializers are defined as static inner classes of the respective classes.


Data should be cached locally. The application will seek out new data under three conditions. One, there is no cached data. Two, the cached data is relatively stale. Three the users position has changed by a significant amount.
  
## Displaying Data

The application uses a tab view to select between viewing data for today or projections by day.

The application uses a RecyclerView instance to display today's data to the user as a single Activity screen. Location and time are displayed at the top, followed by sunrise and sun set then the current forecast conditions. Below the current forecast conditions is a list of hourly forecast conditions. Each hourly item is shown using a short view card. Once clicked an hourly item is displayed using the long view card. a second click reduces the forecast to a short view card. The current conditions are always displayed as long view card.

A RecyclerView instance is also used to display daily projections. Projections are shown using a daily view card and clicking an item switches between the daily view and long daily view card views.