package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_current_cache")
data class WeatherCurrentEntity(
    @PrimaryKey val locationKey: String,
    val weather: String,
    val temperature: Double?,
    val apparentTemperature: Double?,
    val windSpeed: Double?,
    val fetchedAtMillis: Long,
    val expiresAtMillis: Long
)
