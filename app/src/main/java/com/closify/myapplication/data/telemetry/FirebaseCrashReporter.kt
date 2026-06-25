package com.closify.myapplication.data.telemetry

import com.closify.myapplication.core.telemetry.CrashReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics
) : CrashReporter {

    override fun recordException(throwable: Throwable, keys: Map<String, Any?>) {
        keys.forEach { (key, value) -> setKey(key, value?.toString()) }
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setUserId(userId: String?) {
        crashlytics.setUserId(userId.orEmpty())
    }

    override fun setKey(key: String, value: String?) {
        crashlytics.setCustomKey(key, value.orEmpty())
    }
}
