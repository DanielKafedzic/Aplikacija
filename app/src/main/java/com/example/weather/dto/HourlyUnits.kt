package com.example.weather.dto

import com.google.gson.annotations.SerializedName

data class HourlyUnits (

    @SerializedName("time"           ) var time          : String? = null,
    @SerializedName("temperature_2m" ) var temperature2m : String? = null

)