package com.example.weather.api

import com.example.weather.dto.WeatherTime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun getForecast(latitude: Double, longitude: Double): Call<WeatherTime> {
        return retrofit.getForecast(
            latitude.toString(),
            longitude.toString(),
            "true",
            "temperature_2m"
        )
    }
}