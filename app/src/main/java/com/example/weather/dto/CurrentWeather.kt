package com.example.weather.dto

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature") var temperature: Double? = null,
    @SerializedName("windspeed") var windspeed: Double? = null,
    @SerializedName("winddirection") var winddirection: Int? = null,
    @SerializedName("weathercode") var weathercode: Int? = null,
    @SerializedName("is_day") var isDay: Int? = null,
    @SerializedName("time") var time: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString() // Note: No '?' here as readString() cannot be null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(temperature)
        parcel.writeValue(windspeed)
        parcel.writeValue(winddirection)
        parcel.writeValue(weathercode)
        parcel.writeValue(isDay)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentWeather> {
        override fun createFromParcel(parcel: Parcel): CurrentWeather {
            return CurrentWeather(parcel)
        }

        override fun newArray(size: Int): Array<CurrentWeather?> {
            return arrayOfNulls(size)
        }
    }
}