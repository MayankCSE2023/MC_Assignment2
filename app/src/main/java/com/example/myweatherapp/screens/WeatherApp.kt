package com.example.myweatherapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.data.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel) {
    var date by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf<Double?>(null) }
    var minTemp by remember { mutableStateOf<Double?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = date,

            onValueChange = { date = it },
            label = { Text("Enter Date (YYYY-MM-DD)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                coroutineScope.launch {
                    val weatherData = weatherViewModel.getWeatherData(51.5085, -0.1257, date)
                    if (weatherData.time.isNotEmpty()) {
                        maxTemp = weatherData.temperature_2m_max[0]
                        minTemp = weatherData.temperature_2m_min[0]
                    } else {
                        maxTemp = null
                        minTemp = null
                    }
                }
            })
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (maxTemp != null && minTemp != null) {
            Text("Max Temperature: ${maxTemp}°C")
            Text("Min Temperature: ${minTemp}°C")
        } else {
            Text("No data available for the entered date.")
        }
    }
}