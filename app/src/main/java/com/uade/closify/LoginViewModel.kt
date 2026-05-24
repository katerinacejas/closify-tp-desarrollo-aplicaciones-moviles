package com.uade.closify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository = UserRepositoryImpl()) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = LoginUiState.Error("Email inválido")
            return
        }

        if (password.isEmpty()) {
            _uiState.value = LoginUiState.Error("La contraseña no puede estar vacía")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            repository.login(email, password)
                .onSuccess {
                    _uiState.value = LoginUiState.Success
                }
                .onFailure {
                    _uiState.value = LoginUiState.Error(it.message ?: "Error desconocido")
                }
        }
    }
}
