package com.closify.myapplication.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "weather_forecast_cache",
    primaryKeys = ["locationKey", "forecastDate"]
)
data class WeatherForecastEntity(
    val locationKey: String,
    val forecastDate: String,
    val weather: String,
    val temperature: Int,
    val label: String,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long
)
