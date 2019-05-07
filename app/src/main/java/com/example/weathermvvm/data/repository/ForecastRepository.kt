package com.example.weathermvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.weathermvvm.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
}