package com.example.weathermvvm.ui.weather.future.detail

import com.example.weathermvvm.data.provider.UnitProvider
import com.example.weathermvvm.data.repository.ForecastRepository
import com.example.weathermvvm.internal.lazyDeferred
import com.example.weathermvvm.ui.base.BaseWeatherViewModel
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : BaseWeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getFutureWeatherByDate(detailDate, super.isMetricUnit)
    }
}