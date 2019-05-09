package com.example.weathermvvm.data.network.response

import com.google.gson.annotations.SerializedName
import com.example.weathermvvm.data.db.entity.FutureWeatherEntry

data class ForecastDaysContainer(
    @SerializedName("forecastday")
    val entries: List<FutureWeatherEntry>
)