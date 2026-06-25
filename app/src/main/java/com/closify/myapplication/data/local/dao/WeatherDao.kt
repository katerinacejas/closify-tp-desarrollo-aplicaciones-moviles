package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.closify.myapplication.data.local.entity.WeatherCurrentEntity
import com.closify.myapplication.data.local.entity.WeatherForecastEntity

@Dao
interface WeatherDao {

    @Query(
        """
        SELECT * FROM weather_current_cache
        WHERE locationKey = :locationKey AND expiresAtMillis > :nowMillis
        LIMIT 1
        """
    )
    suspend fun getValidCurrent(
        locationKey: String,
        nowMillis: Long
    ): WeatherCurrentEntity?

    @Query(
        """
        SELECT * FROM weather_forecast_cache
        WHERE locationKey = :locationKey
            AND forecastDate >= :startDate
            AND expiresAtMillis > :nowMillis
        ORDER BY forecastDate ASC
        """
    )
    suspend fun getValidForecast(
        locationKey: String,
        startDate: String,
        nowMillis: Long
    ): List<WeatherForecastEntity>

    @Upsert
    suspend fun upsertCurrent(current: WeatherCurrentEntity)

    @Query("DELETE FROM weather_forecast_cache WHERE locationKey = :locationKey")
    suspend fun deleteForecastForLocation(locationKey: String)

    @Upsert
    suspend fun upsertForecast(forecast: List<WeatherForecastEntity>)

    @Query("DELETE FROM weather_current_cache WHERE expiresAtMillis <= :nowMillis")
    suspend fun deleteExpiredCurrent(nowMillis: Long)

    @Query("DELETE FROM weather_forecast_cache WHERE expiresAtMillis <= :nowMillis")
    suspend fun deleteExpiredForecast(nowMillis: Long)
}
