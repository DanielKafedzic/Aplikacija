package com.example.weather

import com.example.weather.dto.CurrentWeather
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.weather.databinding.ActivityWeatherDetailsBinding

class WeatherDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val weatherData = intent.getParcelableExtra<CurrentWeather>("weatherData")
        updateUI(weatherData)
    }

    private fun updateUI(weatherData: CurrentWeather?) {
        binding.temperatureTextView.text = getString(R.string.temperature, weatherData?.temperature ?: 0.0)
        binding.windSpeedTextView.text = getString(R.string.wind_speed, weatherData?.windspeed ?: 0.0)
        binding.winddirectionTextView.text = getString(R.string.wind_direction, weatherData?.winddirection ?: 0)
        binding.weathercodeTextView.text = getString(R.string.weather_code, weatherData?.weathercode ?: 0)
        binding.isdayTextView.text = getString(R.string.is_day, weatherData?.isDay ?: 0)
        binding.timeTextView.text = getString(R.string.time, weatherData?.time ?: "")
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun createBundle(currentWeather: CurrentWeather?): Bundle {
            val bundle = Bundle()
            bundle.putParcelable("weatherData", currentWeather)
            return bundle
        }
    }
}