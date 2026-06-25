package com.closify.myapplication.domain.repository

import com.closify.myapplication.domain.model.DeviceLocation
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import java.time.LocalDate

interface WeatherRepository {
    suspend fun getCurrentWeather(location: DeviceLocation): Result<WeatherCondition>

    suspend fun getPlannerForecast(
        location: DeviceLocation,
        startDate: LocalDate
    ): Result<List<PlannerForecastDay>>
}
