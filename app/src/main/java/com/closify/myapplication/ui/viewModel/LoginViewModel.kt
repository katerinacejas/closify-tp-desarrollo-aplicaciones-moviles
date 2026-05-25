package com.closify.myapplication.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.AppContainer
import com.closify.myapplication.domain.repository.UserRepository
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
    data object ErrorDismissed : LoginEvent
    data object ClearErrors : LoginEvent
}

class LoginViewModel(
    private val userRepository: UserRepository = AppContainer.userRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged    -> _uiState.update { it.copy(email = event.value, emailError = null) }
            is LoginEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value, passwordError = null) }
            is LoginEvent.Submit          -> submit()
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
            userRepository.login(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, generalError = error.message)
                    }
                }
        }
    }
}
