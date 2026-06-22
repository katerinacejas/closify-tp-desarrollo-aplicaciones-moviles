package com.closify.myapplication.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val recoverySent: Boolean = false,
    val generalError: String? = null
)

sealed interface ForgotPasswordEvent {
    data class EmailChanged(val value: String) : ForgotPasswordEvent
    data object Submit : ForgotPasswordEvent
    data object ResetSentState : ForgotPasswordEvent
    data object ErrorDismissed : ForgotPasswordEvent
}

class ForgotPasswordViewModel(
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.value, emailError = null, recoverySent = false) }
            }

            ForgotPasswordEvent.Submit -> submit()
            ForgotPasswordEvent.ResetSentState -> _uiState.update { it.copy(recoverySent = false) }
            ForgotPasswordEvent.ErrorDismissed -> _uiState.update { it.copy(generalError = null) }
        }
    }

    private fun submit() {
        val email = _uiState.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "El email debe ser v\u00E1lido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.requestPasswordRecovery(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, recoverySent = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, generalError = error.message)
                    }
                }
        }
    }
}
