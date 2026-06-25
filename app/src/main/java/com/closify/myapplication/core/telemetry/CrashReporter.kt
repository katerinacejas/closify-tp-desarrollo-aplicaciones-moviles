package com.closify.myapplication.core.telemetry

interface CrashReporter {
    fun recordException(throwable: Throwable, keys: Map<String, Any?> = emptyMap())
    fun log(message: String)
    fun setUserId(userId: String?)
    fun setKey(key: String, value: String?)
}

object NoOpCrashReporter : CrashReporter {
    override fun recordException(throwable: Throwable, keys: Map<String, Any?>) = Unit
    override fun log(message: String) = Unit
    override fun setUserId(userId: String?) = Unit
    override fun setKey(key: String, value: String?) = Unit
}
