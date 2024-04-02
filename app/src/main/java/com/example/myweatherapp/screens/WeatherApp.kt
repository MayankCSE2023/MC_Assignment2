package com.example.myweatherapp.screens
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import com.example.myweatherapp.data.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel, context: Context) {
    var date by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf<Double?>(null) }
    var minTemp by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
                    // Check for internet connectivity
                    if (isInternetConnected(context)) {
                        coroutineScope.launch {
                            try {
                                // Fetch data from the API
                                val weatherData = weatherViewModel.getWeatherData(51.5085, -0.1257, date)
                                if (weatherData.time.isNotEmpty()) {
                                    maxTemp = weatherData.temperature_2m_max[0]
                                    minTemp = weatherData.temperature_2m_min[0]
                                    errorMessage = null // Clear error message if data is available
                                } else {
                                    // Show error message if no data available
                                    maxTemp = null
                                    minTemp = null
                                    errorMessage = "No data found for the entered date."
                                }
                            } catch (e: Exception) {
                                // Handle other exceptions
                                errorMessage = "An error occurred: ${e.message}. Please try another date!!!"
                                // Clear previous output temperature
                                maxTemp = null
                                minTemp = null
                            }
                        }
                    } else {
                        // Show error message for no internet connectivity
                        errorMessage = "No internet connection. Please check your connection."
                        // Clear previous output temperature
                        maxTemp = null
                        minTemp = null
                    }
                } else {
                    // Show error message for invalid date format
                    errorMessage = "Invalid date format. Please enter date in YYYY-MM-DD format."
                    // Clear previous output temperature
                    maxTemp = null
                    minTemp = null
                }
            })
        )
        Spacer(modifier = Modifier.height(16.dp))
        errorMessage?.let {
            // Show error message if present
            Text(it, color = Color.Red)
        }
        if (maxTemp != null && minTemp != null) {
            Text("Max Temperature: ${maxTemp}°C")
            Text("Min Temperature: ${minTemp}°C")
        }
    }
}


fun isValidDate(date: String): Boolean {
    // Perform validation for date format (YYYY-MM-DD)
    val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
    if (!date.matches(regex)) {
        return false
    }

    return true
}


// Function to check for internet connectivity
private fun isInternetConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}
