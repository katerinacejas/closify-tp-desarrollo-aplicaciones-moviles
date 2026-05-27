package com.closify.myapplication.domain.model

data class Weather(
    val condition: WeatherCondition,
    // TODO: para la api quiza sirve
    val temperature: Int? = null,
    val location: String? = null,
    val measuredAt: String = ""
)
