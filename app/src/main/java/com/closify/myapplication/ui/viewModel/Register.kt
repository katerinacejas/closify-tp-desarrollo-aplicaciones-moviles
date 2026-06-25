package com.closify.myapplication.ui.viewmodel

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
import java.util.Calendar

enum class RegisterStep { STEP_1, STEP_2 }

data class RegisterUiState(
    val currentStep: RegisterStep = RegisterStep.STEP_1,

    // Step 1
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Step 2
    val name: String = "",
    val birthDay: String = "",
    val birthMonth: String = "",
    val birthYear: String = "",
    val bio: String = "",

    // Errores step 1
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Errores step 2
    val nameError: String? = null,
    val birthdateError: String? = null,
    val bioError: String? = null,

    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val generalError: String? = null
)

sealed interface RegisterEvent {
    // Step 1
    data class UsernameChanged(val value: String) : RegisterEvent
    data class EmailChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent
    data object NextStep : RegisterEvent

    // Step 2
    data class NameChanged(val value: String) : RegisterEvent
    data class BirthDayChanged(val value: String) : RegisterEvent
    data class BirthMonthChanged(val value: String) : RegisterEvent
    data class BirthYearChanged(val value: String) : RegisterEvent
    data class BioChanged(val value: String) : RegisterEvent
    data object Submit : RegisterEvent
    data class GoogleSignInRequested(val credential: GoogleAuthCredential) : RegisterEvent
    data class GoogleSignInFailed(val message: String?) : RegisterEvent

    data object GoBack : RegisterEvent
    data object ErrorDismissed : RegisterEvent
}

class RegisterViewModel(
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged        -> _uiState.update { it.copy(username = event.value, usernameError = null) }
            is RegisterEvent.EmailChanged           -> _uiState.update { it.copy(email = event.value, emailError = null) }
            is RegisterEvent.PasswordChanged        -> _uiState.update { it.copy(password = event.value, passwordError = null) }
            is RegisterEvent.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.value, confirmPasswordError = null) }
            is RegisterEvent.NextStep               -> validateStep1()
            is RegisterEvent.NameChanged            -> _uiState.update { it.copy(name = event.value, nameError = null) }
            is RegisterEvent.BirthDayChanged        -> _uiState.update { it.copy(birthDay = event.value, birthdateError = null) }
            is RegisterEvent.BirthMonthChanged      -> _uiState.update { it.copy(birthMonth = event.value, birthdateError = null) }
            is RegisterEvent.BirthYearChanged       -> _uiState.update { it.copy(birthYear = event.value, birthdateError = null) }
            is RegisterEvent.BioChanged             -> _uiState.update { it.copy(bio = event.value, bioError = null) }
            is RegisterEvent.Submit                 -> submit()
            is RegisterEvent.GoogleSignInRequested  -> registerWithGoogle(event.credential)
            is RegisterEvent.GoogleSignInFailed     -> onGoogleSignInFailed(event.message)
            is RegisterEvent.GoBack                 -> _uiState.update { it.copy(currentStep = RegisterStep.STEP_1) }
            is RegisterEvent.ErrorDismissed         -> _uiState.update { it.copy(generalError = null) }
        }
    }

    private fun validateStep1() {
        viewModelScope.launch {
            val state = _uiState.value
            var isValid = true

            var usernameError: String? = null
            var emailError: String? = null
            var passwordError: String? = null
            var confirmPasswordError: String? = null

            if (state.username.isEmpty()) {
                usernameError = "El usuario no puede estar vacío."
                isValid = false
            } else if (!userRepository.isUsernameAvailable(state.username)) {
                usernameError = "Ese usuario ya está en uso. Elegí otro."
                isValid = false
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                emailError = "El email debe ser válido."
                isValid = false
            }

            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!_\\-*.])(?=\\S+\$).{8,}\$".toRegex()
            if (!state.password.matches(passwordPattern)) {
                passwordError = "Usá al menos 8 caracteres e incluí una mayúscula, un número y un símbolo."
                isValid = false
            }

            if (state.password != state.confirmPassword) {
                confirmPasswordError = "Las contraseñas no coinciden."
                isValid = false
            }

            _uiState.update {
                it.copy(
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }

            if (isValid) {
                analyticsTracker.track(AnalyticsEvents.registerStepCompleted(step = 1))
                _uiState.update { it.copy(currentStep = RegisterStep.STEP_2) }
            }
        }
    }

    private fun registerWithGoogle(credential: GoogleAuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            analyticsTracker.track(AnalyticsEvents.registerSubmitted(method = "google"))
            userRepository.loginWithGoogle(credential)
                .onSuccess {
                    analyticsTracker.setUserId(userRepository.currentUserId.takeIf { it.isNotBlank() })
                    analyticsTracker.track(AnalyticsEvents.registerSucceeded(method = "google"))
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure { error ->
                    analyticsTracker.track(AnalyticsEvents.registerFailed(error.message, method = "google"))
                    _uiState.update { it.copy(isLoading = false, generalError = error.message) }
                }
        }
    }

    private fun onGoogleSignInFailed(message: String?) {
        analyticsTracker.track(AnalyticsEvents.registerFailed(message, method = "google"))
        _uiState.update {
            it.copy(
                isLoading = false,
                generalError = message ?: "No se pudo registrarte con Google."
            )
        }
    }

    private fun submit() {
        val state = _uiState.value
        var isValid = true

        var nameError: String? = null
        var birthdateError: String? = null
        var bioError: String? = null

        if (state.name.trim().isEmpty()) {
            nameError = "El nombre no puede estar vacío."
            isValid = false
        }

        if (!isValidDate(state.birthDay, state.birthMonth, state.birthYear)) {
            birthdateError = "La fecha de nacimiento debe ser válida."
            isValid = false
        }

        if (state.bio.length > 140) {
            bioError = "La biografía no puede superar los 140 caracteres."
            isValid = false
        }

        _uiState.update {
            it.copy(
                nameError = nameError,
                birthdateError = birthdateError,
                bioError = bioError
            )
        }

        if (!isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            analyticsTracker.track(AnalyticsEvents.registerSubmitted())
            userRepository.register(
                email = state.email,
                password = state.password,
                username = state.username,
                fullName = state.name.trim(),
                birthDate = formatBirthDate(state.birthDay, state.birthMonth, state.birthYear),
                bio = state.bio.trim()
            )
                .onSuccess {
                    analyticsTracker.setUserId(userRepository.currentUserId.takeIf { it.isNotBlank() })
                    analyticsTracker.track(AnalyticsEvents.registerSucceeded())
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure { error ->
                    analyticsTracker.track(AnalyticsEvents.registerFailed(error.message))
                    _uiState.update {
                        it.copy(isLoading = false, generalError = error.message)
                    }
                }
        }
    }

    private fun isValidDate(day: String, month: String, year: String): Boolean {
        return try {
            val d = day.toInt()
            val m = month.toInt()
            val y = year.toInt()

            if (y < 1900 || y > Calendar.getInstance().get(Calendar.YEAR)) return false
            if (m < 1 || m > 12) return false

            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, y)
            cal.set(Calendar.MONTH, m - 1)
            if (d < 1 || d > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) return false

            val birthDate = Calendar.getInstance()
            birthDate.set(y, m - 1, d)
            !birthDate.after(Calendar.getInstance())
        } catch (e: Exception) {
            false
        }
    }

    private fun formatBirthDate(day: String, month: String, year: String): String {
        val monthName = when (month.toInt()) {
            1 -> "enero"
            2 -> "febrero"
            3 -> "marzo"
            4 -> "abril"
            5 -> "mayo"
            6 -> "junio"
            7 -> "julio"
            8 -> "agosto"
            9 -> "septiembre"
            10 -> "octubre"
            11 -> "noviembre"
            12 -> "diciembre"
            else -> ""
        }

        return "${day.toInt()} de $monthName de ${year.toInt()}"
    }
}
