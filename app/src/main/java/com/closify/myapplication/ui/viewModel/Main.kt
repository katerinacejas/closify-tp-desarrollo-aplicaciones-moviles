package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(userRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    var hasLoggedOutOnce = false
        private set

    init {
        if (userRepository.isLoggedIn()) {
            setTelemetryUser(userRepository.currentUserId)
            viewModelScope.launch {
                userRepository.restoreSession()
                setTelemetryUser(userRepository.currentUserId)
            }
        }
    }

    fun onLoginSuccess() {
        setTelemetryUser(userRepository.currentUserId)
        _isLoggedIn.value = true
    }

    fun onLogout() {
        analyticsTracker.track(AnalyticsEvents.logout())
        userRepository.logout()
        OutfitPostRepository.instance.resetSessionSync()
        SocialRepository.instance.resetSessionSync()
        NotificationRepository.instance.resetSessionSync()
        setTelemetryUser(null)
        hasLoggedOutOnce = true
        _isLoggedIn.value = false
    }

    private fun setTelemetryUser(userId: String?) {
        val normalizedUserId = userId?.takeIf { it.isNotBlank() }
        analyticsTracker.setUserId(normalizedUserId)
        crashReporter.setUserId(normalizedUserId)
    }
}
