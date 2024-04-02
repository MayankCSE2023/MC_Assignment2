package com.example.weatherroom

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherroom.data.WeatherViewModel
import com.example.weatherroom.screens.WeatherApp
import com.example.weatherroom.services.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://archive-api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up ViewModel
        viewModel = WeatherViewModel(applicationContext, service)

        // Fetch and save historical data
        fetchAndSaveHistoricalData()

        // Set content view with Compose UI
        setContent {
            WeatherApp(viewModel)
        }
    }

    private fun fetchAndSaveHistoricalData() {
        // Use CoroutineScope to perform asynchronous operation
        CoroutineScope(Dispatchers.Main).launch {
            // Call ViewModel method to fetch and save historical data
            viewModel.fetchAndSaveHistoricalData()
        }
    }
}