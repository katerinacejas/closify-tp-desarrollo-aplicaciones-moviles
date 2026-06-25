package com.closify.myapplication

import android.app.Application
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.FirebaseProfileImageRepository
import com.closify.myapplication.data.repository.GarmentImageRepositoryImpl
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.data.repository.WeatherRepository
import com.closify.myapplication.data.telemetry.FirebaseAnalyticsTracker
import com.closify.myapplication.data.telemetry.FirebaseCrashReporter
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class ClosifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TelemetryProvider.analyticsTracker = FirebaseAnalyticsTracker(FirebaseAnalytics.getInstance(this))
        TelemetryProvider.crashReporter = FirebaseCrashReporter(FirebaseCrashlytics.getInstance())

        FirebaseProfileImageRepository.initialize(this)
        UserRepository.initialize(this)
        GarmentImageRepositoryImpl.initialize(this)
        GarmentRepository.initialize(this)
        NotificationRepository.initialize(this)
        OutfitPostRepository.initialize(this)
        OutfitRepository.initialize(this)
        SocialRepository.initialize(this)
        ProfileRepository.initialize()
        WeatherRepository.initialize(this)
    }
}
