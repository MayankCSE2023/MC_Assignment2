package com.example.weatherroom.data

import android.content.Context
import androidx.room.Room
import com.example.weatherroom.services.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherViewModel(private val context: Context, private val service: WeatherService) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        WeatherDatabase::class.java, "weather_database"
    ).build()

    private val weatherDao = database.weatherDao()

    suspend fun fetchAndSaveHistoricalData() {
        val startDate = "2010-01-01"
        val endDate = "2019-12-31"

        // Check if data is already present in the database
        val dataFromDatabase = withContext(Dispatchers.IO) {
            weatherDao.getWeather(startDate) // Assuming start date can be used to check if data exists
        }

        if (dataFromDatabase == null) {
            // Data not present in the database, fetch from the API
            val response = withContext(Dispatchers.IO) {
                service.getWeather(51.5085, -0.1257, startDate, endDate, "temperature_2m_max,temperature_2m_min")
            }

            // Save data to database
            withContext(Dispatchers.IO) {
                for (i in response.daily.time.indices) {
                    val weatherEntity = WeatherEntity(
                        date = response.daily.time[i],
                        maxTemp = response.daily.temperature_2m_max[i],
                        minTemp = response.daily.temperature_2m_min[i]
                    )
                    weatherDao.insertWeather(weatherEntity)
                }
            }
        }
    }



    suspend fun getWeatherData(date: String): DailyWeather? {
        val weatherDataFromDatabase = getWeatherDataFromDatabase(date)
        return weatherDataFromDatabase ?: run {
            val weatherEntity = weatherDao.getWeather(date)
            weatherEntity?.let {
                DailyWeather(
                    listOf(it.date),
                    listOf(it.maxTemp),
                    listOf(it.minTemp)
                )
            }
        }
    }


    private suspend fun getWeatherDataFromDatabase(date: String): DailyWeather? {
        val weatherEntity = weatherDao.getWeather(date)
        return weatherEntity?.let {
            DailyWeather(
                listOf(it.date),
                listOf(it.maxTemp),
                listOf(it.minTemp)
            )
        }
    }
}