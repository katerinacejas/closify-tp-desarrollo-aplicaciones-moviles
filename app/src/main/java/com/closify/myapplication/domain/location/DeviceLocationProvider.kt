package com.closify.myapplication.domain.location

import com.closify.myapplication.domain.model.DeviceLocation

interface DeviceLocationProvider {
    suspend fun currentLocation(): DeviceLocation?
}
