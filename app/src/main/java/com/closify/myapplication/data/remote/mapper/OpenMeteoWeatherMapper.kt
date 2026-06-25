package com.closify.myapplication.data.remote.mapper

import com.closify.myapplication.data.remote.OpenMeteoCurrentWeather
import com.closify.myapplication.data.remote.OpenMeteoForecastResponse
import com.closify.myapplication.data.remote.OpenMeteoDailyForecast
import com.closify.myapplication.domain.model.CurrentWeatherSummary
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.domain.usecase.ResolveWeatherConditionUseCase
import java.time.LocalDate
import kotlin.math.roundToInt

private val resolveWeatherCondition = ResolveWeatherConditionUseCase()

internal fun OpenMeteoCurrentWeather.toWeatherCondition(): WeatherCondition {
    val apparentOrCurrentTemperature = apparentTemperature ?: temperature ?: DEFAULT_TEMPERATURE_CELSIUS
    val currentWindSpeed = windSpeed ?: DEFAULT_WIND_SPEED_KMH

    return resolveWeatherCondition(
        temperatureCelsius = apparentOrCurrentTemperature,
        windSpeedKmh = currentWindSpeed
    )
}

internal fun OpenMeteoForecastResponse.toCurrentWeatherSummary(): CurrentWeatherSummary? {
    val currentWeather = current ?: return null
    val currentTemperature = currentWeather.apparentTemperature
        ?: currentWeather.temperature
        ?: DEFAULT_TEMPERATURE_CELSIUS
    val minTemperature = daily?.minTemperatures?.firstOrNull()
    val maxTemperature = daily?.maxTemperatures?.firstOrNull()
    val averageTemperature = if (minTemperature != null && maxTemperature != null) {
        (minTemperature + maxTemperature) / 2.0
    } else {
        currentTemperature
    }

    return CurrentWeatherSummary(
        condition = currentWeather.toWeatherCondition(),
        averageTemperature = averageTemperature.roundToInt(),
        minTemperature = (minTemperature ?: minOf(currentTemperature, averageTemperature)).roundToInt(),
        maxTemperature = (maxTemperature ?: maxOf(currentTemperature, averageTemperature)).roundToInt()
    )
}

internal fun OpenMeteoDailyForecast.toPlannerForecastDays(startDate: LocalDate): List<PlannerForecastDay> {
    val dates = time.orEmpty()
    val dailyMaxTemperatures = maxTemperatures.orEmpty()
    val dailyWindSpeeds = maxWindSpeeds.orEmpty()

    return dates.mapIndexedNotNull { index, date ->
        val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull()
            ?: return@mapIndexedNotNull null
        if (parsedDate.isBefore(startDate)) return@mapIndexedNotNull null

        val maxTemperature = dailyMaxTemperatures.getOrNull(index)
            ?: return@mapIndexedNotNull null
        val weather = resolveWeatherCondition(
            temperatureCelsius = maxTemperature,
            windSpeedKmh = dailyWindSpeeds.getOrNull(index) ?: DEFAULT_WIND_SPEED_KMH
        )

        PlannerForecastDay(
            date = parsedDate,
            weather = weather,
            temperature = maxTemperature.roundToInt(),
            label = weather.toWeatherLabel()
        )
    }
}

private fun WeatherCondition.toWeatherLabel(): String = when (this) {
    WeatherCondition.HOT -> "Caluroso"
    WeatherCondition.COLD -> "Frio"
    WeatherCondition.WINDY -> "Ventoso"
    WeatherCondition.MILD -> "Templado"
    WeatherCondition.ANY -> "Indistinto"
}

private const val DEFAULT_TEMPERATURE_CELSIUS = 20.0
private const val DEFAULT_WIND_SPEED_KMH = 0.0
