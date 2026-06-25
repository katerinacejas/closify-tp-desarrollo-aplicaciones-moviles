package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.WeatherCurrentEntity
import com.closify.myapplication.data.local.entity.WeatherForecastEntity
import com.closify.myapplication.domain.model.CurrentWeatherSummary
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import java.time.LocalDate
import kotlin.math.roundToInt

internal fun CurrentWeatherSummary.toCurrentEntity(
    locationKey: String,
    temperature: Double?,
    apparentTemperature: Double?,
    windSpeed: Double?,
    fetchedAtMillis: Long,
    expiresAtMillis: Long
): WeatherCurrentEntity = WeatherCurrentEntity(
    locationKey = locationKey,
    weather = condition.name,
    temperature = temperature,
    apparentTemperature = apparentTemperature,
    averageTemperature = averageTemperature,
    minTemperature = minTemperature,
    maxTemperature = maxTemperature,
    windSpeed = windSpeed,
    fetchedAtMillis = fetchedAtMillis,
    expiresAtMillis = expiresAtMillis
)

internal fun WeatherCurrentEntity.toWeatherConditionOrNull(): WeatherCondition? =
    weather.toWeatherConditionOrNull()

internal fun WeatherCurrentEntity.toCurrentWeatherSummaryOrNull(): CurrentWeatherSummary? {
    val parsedWeather = weather.toWeatherConditionOrNull() ?: return null
    val average = averageTemperature ?: apparentTemperature?.roundToInt() ?: temperature?.roundToInt() ?: return null
    return CurrentWeatherSummary(
        condition = parsedWeather,
        averageTemperature = average,
        minTemperature = minTemperature ?: average,
        maxTemperature = maxTemperature ?: average
    )
}

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
