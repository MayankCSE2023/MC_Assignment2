# Weather Room Android App

## Package Structure

### `com.example.weatherroom`

This package contains the main components of the Weather Room application.

- **MainActivity.kt**: Entry point of the application. It initializes Retrofit service, ViewModel, fetches and saves historical weather data, and sets the content view with the Compose UI.

### `com.example.weatherroom.services`

Contains the service interface for fetching weather data from an API.

- **WeatherService.kt**: Interface defining API endpoints for fetching weather data using Retrofit.

### `com.example.weatherroom.screens`

Contains UI components of the application.

- **WeatherApp.kt**: Composable function representing the main screen where users enter a date to view weather data. Interacts with the ViewModel to fetch and display weather information.

### `com.example.weatherroom.data`

Contains classes related to data management, including Room database entities, DAOs, and ViewModel.

- **WeatherViewModel.kt**: ViewModel for the application. Interacts with Room database and WeatherService to fetch and manage weather data. Contains business logic for handling historical data and error handling.

- **WeatherEntity.kt**: Data class representing the entity for weather data stored in the Room database. Defines the schema for the `weather` table.

- **WeatherDatabase.kt**: Represents the Room database instance for the application. Defines the database configuration and provides access to the DAOs.

- **WeatherDao.kt**: Interface defining the Data Access Object (DAO) for performing database operations such as querying and inserting weather data.

- **WeatherData.kt**: Data class representing the structure of weather data fetched from the API.

- **DailyWeather.kt**: Data class representing daily weather information, including time, maximum temperature, and minimum temperature.

### Other Files

- **`build.gradle`**: Configuration file containing settings for the project, including dependencies and build options.
