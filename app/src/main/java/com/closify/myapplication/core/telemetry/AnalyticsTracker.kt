package com.closify.myapplication.core.telemetry

interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String?)
}

object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun track(event: AnalyticsEvent) = Unit
    override fun setUserId(userId: String?) = Unit
    override fun setUserProperty(name: String, value: String?) = Unit
}
