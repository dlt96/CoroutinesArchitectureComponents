package com.example.weathermvvm.ui.base

import androidx.lifecycle.ViewModel
import com.example.weathermvvm.data.provider.UnitProvider
import com.example.weathermvvm.data.repository.ForecastRepository
import com.example.weathermvvm.internal.UnitSystem
import com.example.weathermvvm.internal.lazyDeferred

abstract class BaseWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()

    val isMetricUnit: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}