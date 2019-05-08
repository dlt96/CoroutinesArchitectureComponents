package com.example.weathermvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.weathermvvm.data.db.CurrentWeatherDao
import com.example.weathermvvm.data.db.WeatherLocationDao
import com.example.weathermvvm.data.db.entity.WeatherLocation
import com.example.weathermvvm.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry
import com.example.weathermvvm.data.network.WeatherNetworkDataSource
import com.example.weathermvvm.data.network.response.CurrentWeatherResponse
import com.example.weathermvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDatasource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
) : ForecastRepository {
    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
         return withContext(Dispatchers.IO) {
             return@withContext weatherLocationDao.getLocation()
         }
    }

    init {
        weatherNetworkDatasource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
       return withContext(Dispatchers.IO){
           initWeatherData()
                return@withContext if (metric) currentWeatherDao.getWeatherMetric()
           else currentWeatherDao.getWeatherImperial()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocation().value

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {//first time we open the app
            fetchCurrentWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()//dummy for the moment
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDatasource.fetchCurrentWeather(
            locationProvider.getPreferredLocationsString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchedTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }


    //note: we can observe forever and global scope launch because we don't have lifecycle unlike a fragment/act.
    //note: withContext returns something unlike .launch which returns optionally a Job
    //note: the "out" in getCurrentWeather is bcs kotlin only let us return directly the interface and not an object which
    //implement the interface
}