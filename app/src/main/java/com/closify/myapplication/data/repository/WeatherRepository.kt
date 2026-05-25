package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.delay

class WeatherRepository {

    companion object {
        val instance = WeatherRepository()
    }

    suspend fun getCurrentWeather(): WeatherCondition {
        // TODO: reemplazar por llamada a API de clima real
        // Ej: OpenWeatherMap, WeatherAPI
        delay(1500)
        return WeatherCondition.MILD
    }
}
