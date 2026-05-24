package com.uade.closify.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.closify.UserRepository
import com.uade.closify.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterStep1ViewModel(private val repository: UserRepository = UserRepositoryImpl()) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError

    fun validateAndProceed(data: RegisterData) {
        viewModelScope.launch {
            var isValid = true

            // Reset errors
            _usernameError.value = null
            _emailError.value = null
            _passwordError.value = null
            _confirmPasswordError.value = null

            // Username validation
            if (data.usuario.isEmpty()) {
                _usernameError.value = "El usuario no puede estar vacío"
                isValid = false
            } else if (!repository.isUsernameAvailable(data.usuario)) {
                _usernameError.value = "Ese usuario ya está en uso. Elegí otro."
                isValid = false
            }

            // Email validation
            if (data.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(data.email).matches()) {
                _emailError.value = "El email debe ser válido."
                isValid = false
            }

            // Password validation
            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$".toRegex()
            if (!data.contrasena.matches(passwordPattern)) {
                _passwordError.value = "Usá al menos 8 caracteres e incluí una mayúscula, un número y un símbolo."
                isValid = false
            }

            // Confirm Password validation
            val confirmPassword = data.contrasena // This should be passed separately or extracted
            // I'll adjust the method signature to accept confirmPassword
        }
    }

    // Refined method
    fun validateStep1(usuario: String, email: String, contrasena: String, confirmacion: String, onValid: (RegisterData) -> Unit) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            var isValid = true

            _usernameError.value = null
            _emailError.value = null
            _passwordError.value = null
            _confirmPasswordError.value = null

            if (usuario.isEmpty()) {
                _usernameError.value = "El usuario no puede estar vacío"
                isValid = false
            } else if (!repository.isUsernameAvailable(usuario)) {
                _usernameError.value = "Ese usuario ya está en uso. Elegí otro."
                isValid = false
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailError.value = "El email debe ser válido."
                isValid = false
            }

            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-*.])(?=\\S+$).{8,}$".toRegex()
            if (!contrasena.matches(passwordPattern)) {
                _passwordError.value = "Usá al menos 8 caracteres e incluí una mayúscula, un número y un símbolo."
                isValid = false
            }

            if (contrasena != confirmacion) {
                _confirmPasswordError.value = "Las contraseñas no coinciden."
                isValid = false
            }

            _uiState.value = RegisterUiState.Idle
            if (isValid) {
                onValid(RegisterData(usuario, email, contrasena))
            }
        }
    }
}
