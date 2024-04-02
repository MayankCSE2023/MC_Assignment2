package com.example.myweatherapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.myweatherapp.data.WeatherViewModel
import com.example.myweatherapp.screens.WeatherApp


// Define MainActivity with Jetpack Compose

// Define MainActivity with Jetpack Compose
class MainActivity : AppCompatActivity() {
    private val viewModel = WeatherViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherApp(viewModel, this@MainActivity)
        }
    }
}