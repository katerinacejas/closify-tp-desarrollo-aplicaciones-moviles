package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.delay
import java.time.LocalDate

class WeatherRepository {

    companion object {
        val instance = WeatherRepository()
    }

    suspend fun getCurrentWeather(): WeatherCondition {
        // TODO: reemplazar por llamada a API de clima real
        // Ej: OpenWeatherMap, WeatherAPI
        delay(1500)
        return MockClosifyData.currentWeather
    }

    fun getPlannerForecast(startDate: LocalDate): List<PlannerForecastDay> {
        val weatherByOffset = listOf(
            WeatherCondition.HOT to (32 to "Caluroso"),
            WeatherCondition.HOT to (30 to "Caluroso"),
            WeatherCondition.HOT to (29 to "Caluroso"),
            WeatherCondition.MILD to (20 to "Templado"),
            WeatherCondition.MILD to (21 to "Templado"),
            WeatherCondition.COLD to (12 to "Lluvioso"),
            WeatherCondition.WINDY to (16 to "Ventoso")
        )

        return weatherByOffset.mapIndexed { index, forecast ->
            PlannerForecastDay(
                date = startDate.plusDays(index.toLong()),
                weather = forecast.first,
                temperature = forecast.second.first,
                label = forecast.second.second
            )
        }
    }
}
