package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.WeatherCurrentEntity
import com.closify.myapplication.data.local.entity.WeatherForecastEntity
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import java.time.LocalDate

internal fun WeatherCondition.toCurrentEntity(
    locationKey: String,
    temperature: Double?,
    apparentTemperature: Double?,
    windSpeed: Double?,
    fetchedAtMillis: Long,
    expiresAtMillis: Long
): WeatherCurrentEntity = WeatherCurrentEntity(
    locationKey = locationKey,
    weather = name,
    temperature = temperature,
    apparentTemperature = apparentTemperature,
    windSpeed = windSpeed,
    fetchedAtMillis = fetchedAtMillis,
    expiresAtMillis = expiresAtMillis
)

internal fun WeatherCurrentEntity.toWeatherConditionOrNull(): WeatherCondition? =
    weather.toWeatherConditionOrNull()

internal fun PlannerForecastDay.toEntity(
    locationKey: String,
    fetchedAtMillis: Long,
    expiresAtMillis: Long
): WeatherForecastEntity = WeatherForecastEntity(
    locationKey = locationKey,
    forecastDate = date.toString(),
    weather = weather.name,
    temperature = temperature,
    label = label,
    fetchedAtMillis = fetchedAtMillis,
    expiresAtMillis = expiresAtMillis
)

internal fun WeatherForecastEntity.toPlannerForecastDayOrNull(): PlannerForecastDay? {
    val parsedDate = runCatching { LocalDate.parse(forecastDate) }.getOrNull() ?: return null
    val parsedWeather = weather.toWeatherConditionOrNull() ?: return null

    return PlannerForecastDay(
        date = parsedDate,
        weather = parsedWeather,
        temperature = temperature,
        label = label
    )
}

private fun String.toWeatherConditionOrNull(): WeatherCondition? =
    runCatching { WeatherCondition.valueOf(this) }
        .getOrNull()
        ?.takeIf { it != WeatherCondition.ANY }
