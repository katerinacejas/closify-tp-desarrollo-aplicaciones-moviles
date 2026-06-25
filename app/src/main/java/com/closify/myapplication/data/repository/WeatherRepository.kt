package com.closify.myapplication.data.repository

import android.util.Log
import com.closify.myapplication.data.remote.OpenMeteoCurrentWeather
import com.closify.myapplication.data.remote.OpenMeteoDailyForecast
import com.closify.myapplication.data.remote.OpenMeteoForecastResponse
import com.closify.myapplication.data.remote.OpenMeteoService
import com.closify.myapplication.domain.model.DeviceLocation
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import kotlin.math.roundToInt

class WeatherRepository {

    companion object {
        val instance = WeatherRepository()
        private const val WEATHER_LOG_TAG = "ClosifyWeather"
    }

    private data class CachedForecast(
        val locationKey: String,
        val fetchedAtMillis: Long,
        val response: OpenMeteoForecastResponse
    )

    private val cacheMutex = Mutex()
    private var cachedForecast: CachedForecast? = null
    private val cacheDurationMillis = 30 * 60 * 1000L

    suspend fun getCurrentWeather(location: DeviceLocation): Result<WeatherCondition> {
        return getForecast(location).mapCatching { response ->
            response.current?.toWeatherCondition()
                ?: error("Open-Meteo did not return current weather.")
        }
    }

    suspend fun getPlannerForecast(
        location: DeviceLocation,
        startDate: LocalDate
    ): Result<List<PlannerForecastDay>> {
        return getForecast(location).mapCatching { response ->
            response.daily?.toPlannerForecastDays(startDate).orEmpty()
        }
    }

    private suspend fun getForecast(location: DeviceLocation) = cacheMutex.withLock {
        val now = System.currentTimeMillis()
        val locationKey = location.toCacheKey()
        val cached = cachedForecast

        if (cached != null &&
            cached.locationKey == locationKey &&
            now - cached.fetchedAtMillis < cacheDurationMillis
        ) {
            return@withLock Result.success(cached.response)
        }

        Log.d(
            WEATHER_LOG_TAG,
            "Requesting Open-Meteo forecast: lat=${location.latitude}, lon=${location.longitude}"
        )

        OpenMeteoService.getForecast(
            latitude = location.latitude,
            longitude = location.longitude,
            forecastDays = 7
        ).onSuccess { response ->
            Log.d(
                WEATHER_LOG_TAG,
                "Open-Meteo current=${response.current?.temperature}, apparent=${response.current?.apparentTemperature}, firstDailyMax=${response.daily?.maxTemperatures?.firstOrNull()}"
            )
            cachedForecast = CachedForecast(
                locationKey = locationKey,
                fetchedAtMillis = now,
                response = response
            )
        }
    }

    private fun OpenMeteoCurrentWeather.toWeatherCondition(): WeatherCondition {
        val apparentOrCurrentTemperature = this.apparentTemperature ?: this.temperature ?: 20.0
        val currentWindSpeed = this.windSpeed ?: 0.0

        return when {
            apparentOrCurrentTemperature >= 27.0 -> WeatherCondition.HOT
            apparentOrCurrentTemperature <= 15.0 -> WeatherCondition.COLD
            currentWindSpeed >= 35.0 -> WeatherCondition.WINDY
            else -> WeatherCondition.MILD
        }
    }

    private fun OpenMeteoDailyForecast.toPlannerForecastDays(startDate: LocalDate): List<PlannerForecastDay> {
        val dates = this.time.orEmpty()
        val dailyMaxTemperatures = this.maxTemperatures.orEmpty()
        val dailyWindSpeeds = this.maxWindSpeeds.orEmpty()

        return dates.mapIndexedNotNull { index, date ->
            val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull() ?: return@mapIndexedNotNull null
            if (parsedDate.isBefore(startDate)) return@mapIndexedNotNull null

            val maxTemperature = dailyMaxTemperatures.getOrNull(index) ?: return@mapIndexedNotNull null
            val displayedTemperature = maxTemperature.roundToInt()
            val weather = mapDailyWeather(
                windSpeed = dailyWindSpeeds.getOrNull(index) ?: 0.0,
                temperature = maxTemperature
            )

            PlannerForecastDay(
                date = parsedDate,
                weather = weather,
                temperature = displayedTemperature,
                label = weather.toWeatherLabel()
            )
        }
    }

    private fun mapDailyWeather(
        windSpeed: Double,
        temperature: Double
    ): WeatherCondition {
        return when {
            temperature >= 27.0 -> WeatherCondition.HOT
            temperature <= 15.0 -> WeatherCondition.COLD
            windSpeed >= 35.0 -> WeatherCondition.WINDY
            else -> WeatherCondition.MILD
        }
    }

    private fun WeatherCondition.toWeatherLabel(): String = when (this) {
        WeatherCondition.HOT -> "Caluroso"
        WeatherCondition.COLD -> "Frio"
        WeatherCondition.WINDY -> "Ventoso"
        WeatherCondition.MILD -> "Templado"
        WeatherCondition.ANY -> "Indistinto"
    }

    private fun DeviceLocation.toCacheKey(): String =
        "${(latitude * 1000).roundToInt()}:${(longitude * 1000).roundToInt()}"
}
