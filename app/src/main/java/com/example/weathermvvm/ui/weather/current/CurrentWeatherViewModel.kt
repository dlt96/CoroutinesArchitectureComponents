package com.example.weathermvvm.ui.weather.current

import com.example.weathermvvm.data.provider.UnitProvider
import com.example.weathermvvm.data.repository.ForecastRepository
import com.example.weathermvvm.internal.lazyDeferred
import com.example.weathermvvm.ui.base.BaseWeatherViewModel

class CurrentWeatherViewModel(
    forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : BaseWeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(super.isMetricUnit)
    }
}
