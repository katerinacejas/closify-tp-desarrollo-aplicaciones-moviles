package com.closify.myapplication.data.telemetry

import android.os.Bundle
import com.closify.myapplication.core.telemetry.AnalyticsEvent
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.parameters.toBundle())
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    private fun Map<String, Any?>.toBundle(): Bundle =
        Bundle().apply {
            forEach { (key, value) ->
                when (value) {
                    null -> Unit
                    is String -> putString(key, value)
                    is Int -> putLong(key, value.toLong())
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putDouble(key, value.toDouble())
                    is Boolean -> putString(key, value.toString())
                    else -> putString(key, value.toString())
                }
            }
        }
}
