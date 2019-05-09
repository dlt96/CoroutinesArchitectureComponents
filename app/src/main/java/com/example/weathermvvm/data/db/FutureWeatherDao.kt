package com.example.weathermvvm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathermvvm.data.db.entity.FutureWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.list.ImperialSimpleFutureWeatherEntry
import com.example.weathermvvm.data.db.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import com.resocoder.forecastmvvm.data.db.unitlocalized.future.detail.ImperialDetailFutureWeatherEntry
import com.resocoder.forecastmvvm.data.db.unitlocalized.future.detail.MetricDetailFutureWeatherEntry
import org.threeten.bp.LocalDate

@Dao
interface FutureWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureWeatherEntry>)

    @Query("SELECT * FROM future_weather WHERE DATE(date) >= DATE(:startDate)")
    fun getSimpleWeatherForecastsMetric(startDate: LocalDate): LiveData<List<MetricSimpleFutureWeatherEntry>>

    @Query("SELECT * FROM future_weather WHERE DATE(date) >= DATE(:startDate)")
    fun getSimpleWeatherForecastsImperial(startDate: LocalDate): LiveData<List<ImperialSimpleFutureWeatherEntry>>

    @Query("SELECT * FROM future_weather WHERE DATE(date) = DATE(:date)")
    fun getDetailedWeatherByDateMetric(date: LocalDate): LiveData<MetricDetailFutureWeatherEntry>

    @Query("SELECT * FROM future_weather WHERE DATE(date) = DATE(:date)")
    fun getDetailedWeatherByDateImperial(date: LocalDate): LiveData<ImperialDetailFutureWeatherEntry>

    @Query("SELECT count(id) FROM future_weather WHERE DATE(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("DELETE FROM future_weather WHERE DATE(date) < DATE(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
}