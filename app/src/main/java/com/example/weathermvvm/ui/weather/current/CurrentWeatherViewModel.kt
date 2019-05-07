package com.example.weathermvvm.ui.weather.current

import androidx.lifecycle.ViewModel;
import com.example.weathermvvm.data.repository.ForecastRepository
import com.example.weathermvvm.internal.UnitSystem
import com.example.weathermvvm.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    private val unitSystem = UnitSystem.METRIC//get from settings

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }
}
