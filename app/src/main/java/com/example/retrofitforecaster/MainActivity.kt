package com.example.retrofitforecaster

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: WeatherAdapter
    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button
    private lateinit var cityNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.setBackgroundColor(Color.parseColor("#FFCC99"))

        cityInput = findViewById(R.id.city_input)
        searchButton = findViewById(R.id.search_button)
        cityNameTextView = findViewById(R.id.city_name)

        adapter = WeatherAdapter()
        findViewById<RecyclerView>(R.id.r_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        
        searchButton.setOnClickListener {
            val cityName = cityInput.text.toString().trim()
            if (cityName.isNotEmpty()) {
                fetchWeather(cityName)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(cityName: String) {
        val apiKey = "29a24e313af4439bd524fa07c6421932"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getWeatherForecast(cityName, apiKey)
                val weatherItems = response.list.map { weather ->
                    val iconCode = weather.weather[0].icon
                    val temperature = "${(weather.main.temp - 273.15).toInt()}°C"
                    val dateTime = java.util.Date(weather.dt * 1000L)
                    val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                    val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

                    val date = dateFormat.format(dateTime)
                    val time = timeFormat.format(dateTime)

                    val iconUrl = "http://openweathermap.org/img/wn/$iconCode@2x.png"
                    WeatherItem(temperature, iconUrl, time, date)
                }
                adapter.submitList(weatherItems)

                cityNameTextView.text = cityName    

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }
}