package com.closify.myapplication.data.local

import com.closify.myapplication.data.local.dao.WeatherDao
import com.closify.myapplication.data.local.entity.WeatherCurrentEntity
import com.closify.myapplication.data.local.entity.WeatherForecastEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class WeatherLocalDataSource(
    private val weatherDao: WeatherDao
) {

    suspend fun getCurrent(
        locationKey: String,
        nowMillis: Long
    ): WeatherCurrentEntity? = withContext(Dispatchers.IO) {
        weatherDao.getValidCurrent(locationKey, nowMillis)
    }

    suspend fun getForecast(
        locationKey: String,
        startDate: LocalDate,
        nowMillis: Long
    ): List<WeatherForecastEntity> = withContext(Dispatchers.IO) {
        weatherDao.getValidForecast(
            locationKey = locationKey,
            startDate = startDate.toString(),
            nowMillis = nowMillis
        )
    }

    suspend fun saveWeather(
        current: WeatherCurrentEntity?,
        forecast: List<WeatherForecastEntity>,
        locationKey: String
    ) = withContext(Dispatchers.IO) {
        current?.let { weatherDao.upsertCurrent(it) }
        weatherDao.deleteForecastForLocation(locationKey)
        if (forecast.isNotEmpty()) {
            weatherDao.upsertForecast(forecast)
        }
    }

    suspend fun deleteExpired(nowMillis: Long) = withContext(Dispatchers.IO) {
        weatherDao.deleteExpiredCurrent(nowMillis)
        weatherDao.deleteExpiredForecast(nowMillis)
    }
}
