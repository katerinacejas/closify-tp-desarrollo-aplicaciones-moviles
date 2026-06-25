package com.closify.myapplication.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.GoogleAuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val generalError: String? = null
)

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object Submit : LoginEvent
    data class GoogleSignInRequested(val credential: GoogleAuthCredential) : LoginEvent
    data class GoogleSignInFailed(val message: String?) : LoginEvent
    data object ErrorDismissed : LoginEvent
    data object ClearErrors : LoginEvent
}

class LoginViewModel(
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged    -> _uiState.update { it.copy(email = event.value, emailError = null) }
            is LoginEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value, passwordError = null) }
            is LoginEvent.Submit          -> submit()
            is LoginEvent.GoogleSignInRequested -> loginWithGoogle(event.credential)
            is LoginEvent.GoogleSignInFailed -> onGoogleSignInFailed(event.message)
            is LoginEvent.ErrorDismissed  -> _uiState.update { it.copy(generalError = null) }
            is LoginEvent.ClearErrors     -> _uiState.update { it.copy(emailError = null, passwordError = null, generalError = null) }
        }
    }

    private fun submit() {
        val state = _uiState.value
        var isValid = true

        var emailError: String? = null
        var passwordError: String? = null

        if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "El email debe ser válido."
            isValid = false
        }

        if (state.password.isEmpty()) {
            passwordError = "La contraseña no puede estar vacía."
            isValid = false
        }

        if (!isValid) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            analyticsTracker.track(AnalyticsEvents.loginSubmitted())
            userRepository.login(state.email, state.password)
                .onSuccess {
                    analyticsTracker.setUserId(userRepository.currentUserId.takeIf { it.isNotBlank() })
                    analyticsTracker.track(AnalyticsEvents.loginSucceeded())
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                .onFailure { error ->
                    analyticsTracker.track(AnalyticsEvents.loginFailed(error.message))
                    _uiState.update {
                        it.copy(isLoading = false, generalError = error.message)
                    }
                }
        }
    }

    private fun loginWithGoogle(credential: GoogleAuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            analyticsTracker.track(AnalyticsEvents.loginSubmitted(method = "google"))
            userRepository.loginWithGoogle(credential)
                .onSuccess {
                    analyticsTracker.setUserId(userRepository.currentUserId.takeIf { it.isNotBlank() })
                    analyticsTracker.track(AnalyticsEvents.loginSucceeded(method = "google"))
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                .onFailure { error ->
                    analyticsTracker.track(AnalyticsEvents.loginFailed(error.message, method = "google"))
                    _uiState.update { it.copy(isLoading = false, generalError = error.message) }
                }
        }
    }

    private fun onGoogleSignInFailed(message: String?) {
        analyticsTracker.track(AnalyticsEvents.loginFailed(message, method = "google"))
        _uiState.update {
            it.copy(
                isLoading = false,
                generalError = message ?: "No se pudo iniciar sesi\u00F3n con Google."
            )
        }
    }
}
