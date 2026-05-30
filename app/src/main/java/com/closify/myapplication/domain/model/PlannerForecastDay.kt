package com.closify.myapplication.domain.model

import java.time.LocalDate

data class PlannerForecastDay(
    val date: LocalDate,
    val weather: WeatherCondition,
    val temperature: Int,
    val label: String
)
