package com.closify.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.WeatherLocalDataSource
import com.closify.myapplication.data.local.mapper.toCurrentEntity
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toPlannerForecastDayOrNull
import com.closify.myapplication.data.local.mapper.toWeatherConditionOrNull
import com.closify.myapplication.data.network.NetworkMonitor
import com.closify.myapplication.data.remote.OpenMeteoRemoteDataSource
import com.closify.myapplication.data.remote.mapper.toPlannerForecastDays
import com.closify.myapplication.data.remote.mapper.toWeatherCondition
import com.closify.myapplication.domain.model.DeviceLocation
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import kotlin.math.roundToInt
import com.closify.myapplication.domain.repository.WeatherRepository as WeatherRepositoryContract

class WeatherRepository : WeatherRepositoryContract {

    companion object {
        val instance = WeatherRepository()

        private const val WEATHER_LOG_TAG = "ClosifyWeather"
        private const val CACHE_DURATION_MILLIS = 30 * 60 * 1000L

        fun initialize(context: Context) {
            instance.localDataSource = WeatherLocalDataSource(
                AppDatabase.getInstance(context).weatherDao()
            )
            instance.networkMonitor = NetworkMonitor(context)
        }
    }

    private data class CachedWeather(
        val locationKey: String,
        val fetchedAtMillis: Long,
        val currentWeather: WeatherCondition?,
        val forecastDays: List<PlannerForecastDay>
    ) {
        fun isFresh(nowMillis: Long): Boolean =
            nowMillis - fetchedAtMillis < CACHE_DURATION_MILLIS
    }

    private val cacheMutex = Mutex()
    private val remoteDataSource = OpenMeteoRemoteDataSource()
    private var memoryCache: CachedWeather? = null
    private var localDataSource: WeatherLocalDataSource? = null
    private var networkMonitor: NetworkMonitor? = null

    override suspend fun getCurrentWeather(location: DeviceLocation): Result<WeatherCondition> =
        cacheMutex.withLock {
            val now = System.currentTimeMillis()
            val locationKey = location.toCacheKey()

            memoryCache
                ?.takeIf { it.locationKey == locationKey && it.isFresh(now) }
                ?.currentWeather
                ?.let { return@withLock Result.success(it) }

            val localCurrent = try {
                localDataSource?.deleteExpired(now)
                localDataSource?.getCurrent(locationKey, now)
            } catch (error: Exception) {
                Log.w(WEATHER_LOG_TAG, "Unable to read current weather cache.", error)
                null
            }

            localCurrent
                ?.toWeatherConditionOrNull()
                ?.let { cachedWeather ->
                    memoryCache = CachedWeather(
                        locationKey = locationKey,
                        fetchedAtMillis = now,
                        currentWeather = cachedWeather,
                        forecastDays = memoryCache
                            ?.takeIf { it.locationKey == locationKey && it.isFresh(now) }
                            ?.forecastDays
                            .orEmpty()
                    )
                    return@withLock Result.success(cachedWeather)
                }

            fetchAndCacheWeather(
                location = location,
                locationKey = locationKey,
                startDate = LocalDate.now(),
                nowMillis = now
            ).mapCatching { weather ->
                weather.currentWeather ?: error("Open-Meteo did not return current weather.")
            }
        }

    override suspend fun getPlannerForecast(
        location: DeviceLocation,
        startDate: LocalDate
    ): Result<List<PlannerForecastDay>> = cacheMutex.withLock {
        val now = System.currentTimeMillis()
        val locationKey = location.toCacheKey()

        memoryCache
            ?.takeIf { it.locationKey == locationKey && it.isFresh(now) }
            ?.forecastDays
            ?.filter { !it.date.isBefore(startDate) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { return@withLock Result.success(it) }

        val localForecast = try {
            localDataSource?.deleteExpired(now)
            localDataSource
                ?.getForecast(locationKey, startDate, now)
                .orEmpty()
                .mapNotNull { it.toPlannerForecastDayOrNull() }
        } catch (error: Exception) {
            Log.w(WEATHER_LOG_TAG, "Unable to read forecast cache.", error)
            emptyList()
        }

        if (localForecast.isNotEmpty()) {
            memoryCache = CachedWeather(
                locationKey = locationKey,
                fetchedAtMillis = now,
                currentWeather = memoryCache
                    ?.takeIf { it.locationKey == locationKey && it.isFresh(now) }
                    ?.currentWeather,
                forecastDays = localForecast
            )
            return@withLock Result.success(localForecast)
        }

        fetchAndCacheWeather(
            location = location,
            locationKey = locationKey,
            startDate = startDate,
            nowMillis = now
        ).map { weather ->
            weather.forecastDays.filter { !it.date.isBefore(startDate) }
        }
    }

    private suspend fun fetchAndCacheWeather(
        location: DeviceLocation,
        locationKey: String,
        startDate: LocalDate,
        nowMillis: Long
    ): Result<CachedWeather> {
        if (networkMonitor?.isOnline() == false) {
            return Result.failure(IllegalStateException("No network available for weather request."))
        }

        Log.d(
            WEATHER_LOG_TAG,
            "Requesting Open-Meteo forecast: lat=${location.latitude}, lon=${location.longitude}"
        )

        val response = remoteDataSource.getForecast(
            latitude = location.latitude,
            longitude = location.longitude,
            forecastDays = 7
        ).getOrElse { error ->
            return Result.failure(error)
        }

        Log.d(
            WEATHER_LOG_TAG,
            "Open-Meteo current=${response.current?.temperature}, apparent=${response.current?.apparentTemperature}, firstDailyMax=${response.daily?.maxTemperatures?.firstOrNull()}"
        )

        val currentWeather = response.current?.toWeatherCondition()
        val forecastDays = response.daily?.toPlannerForecastDays(startDate).orEmpty()
        val expiresAtMillis = nowMillis + CACHE_DURATION_MILLIS

        try {
            localDataSource?.saveWeather(
                current = currentWeather?.toCurrentEntity(
                    locationKey = locationKey,
                    temperature = response.current?.temperature,
                    apparentTemperature = response.current?.apparentTemperature,
                    windSpeed = response.current?.windSpeed,
                    fetchedAtMillis = nowMillis,
                    expiresAtMillis = expiresAtMillis
                ),
                forecast = forecastDays.map { day ->
                    day.toEntity(
                        locationKey = locationKey,
                        fetchedAtMillis = nowMillis,
                        expiresAtMillis = expiresAtMillis
                    )
                },
                locationKey = locationKey
            )
        } catch (error: Exception) {
            Log.w(WEATHER_LOG_TAG, "Unable to write weather cache.", error)
        }

        return Result.success(
            CachedWeather(
                locationKey = locationKey,
                fetchedAtMillis = nowMillis,
                currentWeather = currentWeather,
                forecastDays = forecastDays
            ).also { memoryCache = it }
        )
    }

    private fun DeviceLocation.toCacheKey(): String =
        "${(latitude * 1000).roundToInt()}:${(longitude * 1000).roundToInt()}"
}
