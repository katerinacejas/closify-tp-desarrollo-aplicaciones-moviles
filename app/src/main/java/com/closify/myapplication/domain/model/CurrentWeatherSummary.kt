package com.closify.myapplication.domain.model

data class CurrentWeatherSummary(
    val condition: WeatherCondition,
    val averageTemperature: Int,
    val minTemperature: Int,
    val maxTemperature: Int
)
