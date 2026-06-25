package com.closify.myapplication.data.remote

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CancellationException
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class OpenMeteoForecastResponse(
    val current: OpenMeteoCurrentWeather?,
    val daily: OpenMeteoDailyForecast?
)

data class OpenMeteoCurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double?,
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double?,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double?
)

data class OpenMeteoDailyForecast(
    val time: List<String>?,
    @SerializedName("temperature_2m_max")
    val maxTemperatures: List<Double>?,
    @SerializedName("wind_speed_10m_max")
    val maxWindSpeeds: List<Double>?
)

internal interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,wind_speed_10m",
        @Query("daily") daily: String = "temperature_2m_max,wind_speed_10m_max",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoForecastResponse
}

object OpenMeteoService {
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .writeTimeout(4, TimeUnit.SECONDS)
            .callTimeout(6, TimeUnit.SECONDS)
            .build()
    }

    internal val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}

internal class OpenMeteoRemoteDataSource(
    private val api: OpenMeteoApi = OpenMeteoService.api
) {

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        forecastDays: Int = 7
    ): Result<OpenMeteoForecastResponse> {
        return try {
            Result.success(
                api.getForecast(
                    latitude = latitude,
                    longitude = longitude,
                    forecastDays = forecastDays
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
