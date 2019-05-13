package com.example.weathermvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.weathermvvm.data.db.CurrentWeatherDao
import com.example.weathermvvm.data.db.FutureWeatherDao
import com.example.weathermvvm.data.db.WeatherLocationDao
import com.example.weathermvvm.data.db.entity.WeatherLocation
import com.example.weathermvvm.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.example.weathermvvm.data.network.FORECAST_DAYS_COUNT
import com.example.weathermvvm.data.network.WeatherNetworkDataSource
import com.example.weathermvvm.data.network.response.CurrentWeatherResponse
import com.example.weathermvvm.data.network.response.FutureWeatherResponse
import com.example.weathermvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDatasource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider,
    private val futureWeatherDao: FutureWeatherDao
) : ForecastRepository {

    init {
        weatherNetworkDatasource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }

    override suspend fun getFutureWeatherByDate(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out UnitSpecificDetailFutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) futureWeatherDao.getDetailedWeatherByDateMetric(startDate)
            else futureWeatherDao.getDetailedWeatherByDateImperial(startDate)
        }
    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
            else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
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

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {//first time we open the app
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()

        if (isFetchFutureNeeded())
            fetchFutureWeather()
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDatasource.fetchCurrentWeather(
            locationProvider.getPreferredLocationsString(),
            Locale.getDefault().language
        )
    }

    private suspend fun fetchFutureWeather() {
        weatherNetworkDatasource.fetchFutureWeather(
            locationProvider.getPreferredLocationsString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchedTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }

    //note: we can observe forever and global scope launch because we don't have lifecycle unlike a fragment/act.
    //note: withContext returns something unlike .launch which returns optionally a Job
    //note: the "out" in getCurrentWeather is bcs kotlin only let us return directly the interface and not an object which
    //implement the interface
}