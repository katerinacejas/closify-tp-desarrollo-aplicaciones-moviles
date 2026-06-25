package com.closify.myapplication.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.closify.myapplication.data.location.AndroidDeviceLocationProvider
import com.closify.myapplication.domain.model.DeviceLocation
import kotlinx.coroutines.launch

@Composable
fun rememberDeviceLocationRequester(
    onLocationAvailable: (DeviceLocation) -> Unit,
    onLocationUnavailable: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val locationProvider = remember(context) {
        AndroidDeviceLocationProvider(context.applicationContext)
    }
    val scope = rememberCoroutineScope()
    val currentOnLocationAvailable by rememberUpdatedState(onLocationAvailable)
    val currentOnLocationUnavailable by rememberUpdatedState(onLocationUnavailable)

    fun requestDeviceLocation() {
        scope.launch {
            val location = locationProvider.currentLocation()
            if (location == null) {
                currentOnLocationUnavailable()
            } else {
                currentOnLocationAvailable(location)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            requestDeviceLocation()
        } else {
            currentOnLocationUnavailable()
        }
    }

    return remember(context) {
        {
            if (context.hasLocationPermission()) {
                requestDeviceLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
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
