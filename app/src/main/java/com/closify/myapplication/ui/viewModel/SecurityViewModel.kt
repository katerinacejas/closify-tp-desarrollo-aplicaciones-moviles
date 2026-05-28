package com.closify.myapplication.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SecurityViewModel : ViewModel() {

    private val _currentPassword = MutableStateFlow("")
    val currentPassword: StateFlow<String> = _currentPassword.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmError = MutableStateFlow<String?>(null)
    val confirmError: StateFlow<String?> = _confirmError.asStateFlow()

    fun onCurrentPasswordChange(password: String) {
        _currentPassword.value = password
    }

    fun onNewPasswordChange(password: String) {
        _newPassword.value = password
        validateNewPassword(password)
        validateConfirmPassword(_confirmPassword.value)
    }

    fun onConfirmPasswordChange(password: String) {
        _confirmPassword.value = password
        validateConfirmPassword(password)
    }

    private fun validateNewPassword(password: String) {
        val pattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!_\\-*.])(?=\\S+\$).{8,}\$".toRegex()
        _passwordError.value = if (password.isNotEmpty() && !password.matches(pattern)) {
            "Usá al menos 8 caracteres e incluí una mayúscula, un número y un símbolo."
        } else {
            null
        }
    }

    private fun validateConfirmPassword(password: String) {
        _confirmError.value = if (password.isNotEmpty() && password != _newPassword.value) {
            "Las contraseñas no coinciden"
        } else {
            null
        }
    }

    fun changePassword() {
        // Lógica para impactar en base de datos
    }
}
