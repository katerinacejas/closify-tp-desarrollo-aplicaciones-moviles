package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SecurityViewModel(
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _currentPassword = MutableStateFlow("")
    val currentPassword: StateFlow<String> = _currentPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _currentPasswordError = MutableStateFlow<String?>(null)
    val currentPasswordError: StateFlow<String?> = _currentPasswordError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmError = MutableStateFlow<String?>(null)
    val confirmError: StateFlow<String?> = _confirmError.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun onCurrentPasswordChange(password: String) {
        _currentPassword.value = password
        _currentPasswordError.value = null
        _successMessage.value = null
    }

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
        _successMessage.value = null
        validateNewPassword(password)
        validateConfirmPassword(_confirmPassword.value)
    }

    fun onConfirmPasswordChange(password: String) {
        _confirmPassword.value = password
        _successMessage.value = null
        validateConfirmPassword(password)
    }

    fun changePassword() {
        validateCurrentPassword()
        validateNewPassword(_newPassword.value)
        validateConfirmPassword(_confirmPassword.value)

        if (_currentPasswordError.value != null || _passwordError.value != null || _confirmError.value != null) {
            return
        }

        userRepository.changeCurrentUserPassword(
            currentPassword = _currentPassword.value,
            newPassword = _newPassword.value
        ).onSuccess {
            _currentPassword.value = ""
            _newPassword.value = ""
            _confirmPassword.value = ""
            _currentPasswordError.value = null
            _passwordError.value = null
            _confirmError.value = null
            _successMessage.value = "Contraseña actualizada correctamente."
        }.onFailure { error ->
            _currentPasswordError.value = error.message
            _successMessage.value = null
        }
    }

    private fun validateCurrentPassword() {
        _currentPasswordError.value = if (_currentPassword.value.isBlank()) {
            "La contraseña actual no puede estar vacía."
        } else {
            null
        }
    }

    private fun validateNewPassword(password: String) {
        val pattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!_\\-*.])(?=\\S+\$).{8,}\$".toRegex()
        _passwordError.value = when {
            password.isBlank() -> "La nueva contraseña no puede estar vacía."
            !password.matches(pattern) -> "Usá al menos 8 caracteres e incluí una mayúscula, un número y un símbolo."
            password == _currentPassword.value -> "La nueva contraseña debe ser distinta a la actual."
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String) {
        _confirmError.value = when {
            password.isBlank() -> "Confirmá la nueva contraseña."
            password != _newPassword.value -> "Las contraseñas no coinciden."
            else -> null
        }
    }
}
