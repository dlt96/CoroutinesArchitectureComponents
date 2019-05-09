package com.example.weathermvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.weathermvvm.data.db.entity.WeatherLocation
import com.example.weathermvvm.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
    suspend fun getWeatherLocation():LiveData<WeatherLocation>
}