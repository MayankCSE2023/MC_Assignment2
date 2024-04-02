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



//    suspend fun getWeatherData(date: String): DailyWeather? {
//        val weatherDataFromDatabase = getWeatherDataFromDatabase(date)
//        return weatherDataFromDatabase ?: run {
//            val weatherEntity = weatherDao.getWeather(date)
//            weatherEntity?.let {
//                DailyWeather(
//                    listOf(it.date),
//                    listOf(it.maxTemp),
//                    listOf(it.minTemp)
//                )
//            }
//        }
//    }

    suspend fun getWeatherData(date: String): DailyWeather? {
        val weatherDataFromDatabase = getWeatherDataFromDatabase(date)
        return weatherDataFromDatabase ?: run {
            if (isYearAbove2019(date)) {
                // Calculate average temperature for the previous years' data for the same day and month
                val previousYearsWeatherData = getPreviousYearsWeatherData(date)
                if (previousYearsWeatherData.isNotEmpty()) {
                    val maxTempAvg = previousYearsWeatherData.map { it.maxTemp }.average().format(2)
                    val minTempAvg = previousYearsWeatherData.map { it.minTemp }.average().format(2)
                    DailyWeather(listOf(date), listOf(maxTempAvg.toDouble()), listOf(minTempAvg.toDouble()))
                } else {
                    null // Handle case when no data available for previous years
                }
            } else {
                // Fetch data from the database
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
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private suspend fun getPreviousYearsWeatherData(date: String): List<WeatherEntity> {
        val year = date.substring(0, 4).toInt()
        val previousYearsData = mutableListOf<WeatherEntity>()
        for (prevYear in 2010 until 2020) {
            val prevYearDate = "$prevYear${date.substring(4)}"
            val weatherEntity = weatherDao.getWeather(prevYearDate)
            weatherEntity?.let {
                previousYearsData.add(it)
            }
        }
        return previousYearsData
    }

    private fun isYearAbove2019(date: String): Boolean {
        val year = date.substring(0, 4).toInt()
        return year > 2019
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