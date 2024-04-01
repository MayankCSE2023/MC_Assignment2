package com.example.myweatherapp.data

import android.util.Log
import com.example.myweatherapp.services.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Define ViewModel to handle API requests

class WeatherViewModel {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://archive-api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    suspend fun getWeatherData(latitude: Double, longitude: Double, date: String): DailyWeather {
        val response = service.getWeather(latitude, longitude, date, date, "temperature_2m_max,temperature_2m_min")
        return response.daily
    }
}
