package com.closify.myapplication.core.telemetry

object TelemetryProvider {
    @Volatile
    var analyticsTracker: AnalyticsTracker = NoOpAnalyticsTracker

    @Volatile
    var crashReporter: CrashReporter = NoOpCrashReporter
}
