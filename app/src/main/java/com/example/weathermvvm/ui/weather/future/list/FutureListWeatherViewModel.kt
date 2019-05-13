package com.example.weathermvvm.ui.weather.future.list

import com.example.weathermvvm.data.provider.UnitProvider
import com.example.weathermvvm.data.repository.ForecastRepository
import com.example.weathermvvm.internal.lazyDeferred
import com.example.weathermvvm.ui.base.BaseWeatherViewModel
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : BaseWeatherViewModel(forecastRepository, unitProvider) {

    val weatherEntries by lazyDeferred {
        forecastRepository.getFutureWeatherList(LocalDate.now(), super.isMetricUnit)
    }
}
