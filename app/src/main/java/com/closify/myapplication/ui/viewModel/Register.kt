package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.UserRepository
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

    data object GoBack : RegisterEvent
    data object ErrorDismissed : RegisterEvent
}

class RegisterViewModel(
    private val userRepository: UserRepository = UserRepository.instance
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
                _uiState.update { it.copy(currentStep = RegisterStep.STEP_2) }
            }
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
            userRepository.register(state.email, state.password, state.username)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure { error ->
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
}
