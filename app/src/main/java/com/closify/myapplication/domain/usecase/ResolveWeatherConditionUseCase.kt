package com.closify.myapplication.domain.usecase

import com.closify.myapplication.domain.model.WeatherCondition

class ResolveWeatherConditionUseCase {

    operator fun invoke(
        temperatureCelsius: Double,
        windSpeedKmh: Double
    ): WeatherCondition = when {
        temperatureCelsius >= HOT_THRESHOLD_CELSIUS -> WeatherCondition.HOT
        temperatureCelsius <= COLD_THRESHOLD_CELSIUS -> WeatherCondition.COLD
        windSpeedKmh >= STRONG_WIND_THRESHOLD_KMH -> WeatherCondition.WINDY
        else -> WeatherCondition.MILD
    }

    private companion object {
        private const val HOT_THRESHOLD_CELSIUS = 27.0
        private const val COLD_THRESHOLD_CELSIUS = 15.0
        private const val STRONG_WIND_THRESHOLD_KMH = 35.0
    }
}
