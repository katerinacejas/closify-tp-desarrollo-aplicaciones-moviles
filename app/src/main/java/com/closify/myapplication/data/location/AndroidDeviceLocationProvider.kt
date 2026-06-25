package com.closify.myapplication.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import com.closify.myapplication.domain.location.DeviceLocationProvider
import com.closify.myapplication.domain.model.DeviceLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class AndroidDeviceLocationProvider(
    context: Context
) : DeviceLocationProvider {

    private val appContext = context.applicationContext

    override suspend fun currentLocation(): DeviceLocation? {
        if (!appContext.hasLocationPermission()) return null

        val now = System.currentTimeMillis()
        cachedDeviceLocation?.takeIf { it.isFresh(now) }?.let { cachedLocation ->
            return cachedLocation.location
        }

        val location = appContext.currentAndroidLocation() ?: return null
        Log.d(
            LOCATION_LOG_TAG,
            "Using device location: lat=${location.latitude}, lon=${location.longitude}, provider=${location.provider}"
        )

        return DeviceLocation(
            latitude = location.latitude,
            longitude = location.longitude
        ).also { deviceLocation ->
            cachedDeviceLocation = CachedDeviceLocation(
                location = deviceLocation,
                cachedAtMillis = System.currentTimeMillis()
            )
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun Context.currentAndroidLocation(): Location? {
        val now = System.currentTimeMillis()
        val elapsedNowNanos = SystemClock.elapsedRealtimeNanos()
        val client = LocationServices.getFusedLocationProviderClient(this)
        val fusedLastLocation = runCatching { client.lastLocation.await() }.getOrNull()?.takeIfValid()
        if (fusedLastLocation?.isFresh(now, elapsedNowNanos) == true) return fusedLastLocation

        val systemLastLocation = lastKnownSystemLocation()
        if (systemLastLocation?.isFresh(now, elapsedNowNanos) == true) return systemLastLocation

        return withTimeoutOrNull(CURRENT_LOCATION_TIMEOUT_MILLIS) {
            runCatching {
                client.getCurrentLocation(
                    currentLocationPriority(),
                    CancellationTokenSource().token
                ).await()
            }.getOrNull()
        }?.takeIfValid()
    }

    @SuppressLint("MissingPermission")
    private fun Context.lastKnownSystemLocation(): Location? {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        return providers
            .mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }
                    .getOrNull()
                    ?.takeIfValid()
            }
            .maxByOrNull { it.time }
    }

    private fun Context.currentLocationPriority(): Int =
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

    private fun Context.hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    private fun CachedDeviceLocation.isFresh(now: Long): Boolean =
        now - cachedAtMillis < LOCATION_CACHE_DURATION_MILLIS

    private fun Location.isFresh(now: Long, elapsedNowNanos: Long): Boolean {
        val elapsedAgeMillis = if (elapsedRealtimeNanos > 0L) {
            (elapsedNowNanos - elapsedRealtimeNanos) / 1_000_000L
        } else {
            Long.MAX_VALUE
        }
        val wallClockAgeMillis = if (time > 0L) now - time else Long.MAX_VALUE

        return elapsedAgeMillis < LOCATION_CACHE_DURATION_MILLIS ||
            wallClockAgeMillis < LOCATION_CACHE_DURATION_MILLIS
    }

    private fun Location.takeIfValid(): Location? =
        takeIf {
            latitude in -90.0..90.0 &&
                longitude in -180.0..180.0 &&
                !(latitude == 0.0 && longitude == 0.0)
        }

    private data class CachedDeviceLocation(
        val location: DeviceLocation,
        val cachedAtMillis: Long
    )

    private companion object {
        private const val LOCATION_CACHE_DURATION_MILLIS = 30 * 60 * 1000L
        private const val CURRENT_LOCATION_TIMEOUT_MILLIS = 4_000L
        private const val LOCATION_LOG_TAG = "ClosifyLocation"

        private var cachedDeviceLocation: CachedDeviceLocation? = null
    }
}
