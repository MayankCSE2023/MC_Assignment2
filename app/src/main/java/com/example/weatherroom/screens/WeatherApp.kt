package com.example.weatherroom.screens


import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.weatherroom.data.WeatherViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel, context: Context) {
    var date by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf<Double?>(null) }
    var minTemp by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // New

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
                if (isValidDate(date)) {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            // Fetch data from the database or API
                            val weatherData = weatherViewModel.getWeatherData(date)
                            if (weatherData != null) {
                                if (weatherData.time.isNotEmpty()) {
                                    maxTemp = weatherData.temperature_2m_max[0]
                                    minTemp = weatherData.temperature_2m_min[0]
                                    errorMessage = null // Clear error message if data is available
                                } else {
                                    // Show error message and clear input field
                                    maxTemp = null
                                    minTemp = null
                                    errorMessage = "No data found for the entered date."
                                    date = ""
                                }
                            } else {
                                // Show error message and clear input field
                                maxTemp = null
                                minTemp = null
                                errorMessage = "An error occurred while fetching weather data."
                                date = ""
                            }
                        } catch (e: Exception) {
                            // Handle error
                            // Show error message to user
                            // Example: showToast(context, "Failed to fetch weather data")
                            maxTemp = null
                            minTemp = null
                            errorMessage = "An error occurred: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    // Show error message to user for invalid date format
                    // Example: showToast(context, "Invalid date format. Please enter date in YYYY-MM-DD format.")
                    errorMessage = "Invalid date format. Please enter date in YYYY-MM-DD format."
                }

            })
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            // Show loading indicator
            // Example: CircularProgressIndicator()
            Text("Loading...")
        } else {
            errorMessage?.let {
                // Show error message if present
                Text(it, color = Color.Red)
            }
            if (maxTemp != null && minTemp != null) {
                Text("Max Temperature: ${maxTemp}°C")
                Text("Min Temperature: ${minTemp}°C")
            } else {
                Text("No data available for the entered date.")
            }
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


fun isValidDate(date: String): Boolean {
    // Perform validation for date format (YYYY-MM-DD)
    val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
    if (!date.matches(regex)) {
        return false
    }

    return true
}
