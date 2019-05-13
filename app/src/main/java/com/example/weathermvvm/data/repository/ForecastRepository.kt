package com.example.weathermvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.weathermvvm.data.db.entity.WeatherLocation
import com.example.weathermvvm.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
    suspend fun getWeatherLocation():LiveData<WeatherLocation>
    suspend fun getFutureWeatherList(startDate: LocalDate, metric: Boolean): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>>
    suspend fun getFutureWeatherByDate(date: LocalDate, metric: Boolean): LiveData<out UnitSpecificDetailFutureWeatherEntry>
}