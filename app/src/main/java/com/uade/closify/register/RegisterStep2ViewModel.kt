package com.uade.closify.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.closify.UserRepository
import com.uade.closify.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterStep2ViewModel(private val repository: UserRepository = UserRepositoryImpl()) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _birthdateError = MutableStateFlow<String?>(null)
    val birthdateError: StateFlow<String?> = _birthdateError

    private val _bioError = MutableStateFlow<String?>(null)
    val bioError: StateFlow<String?> = _bioError

    fun register(data: RegisterData, day: String, month: String, year: String) {
        viewModelScope.launch {
            var isValid = true
            _nameError.value = null
            _birthdateError.value = null
            _bioError.value = null

            if (data.nombre.trim().isEmpty()) {
                _nameError.value = "El nombre no puede estar vacío."
                isValid = false
            }

            if (!isValidDate(day, month, year)) {
                _birthdateError.value = "La fecha de nacimiento debe ser válida."
                isValid = false
            }

            if (data.biografia.length > 140) {
                _bioError.value = "La biografía no puede superar los 140 caracteres."
                isValid = false
            }

            if (isValid) {
                _uiState.value = RegisterUiState.Loading
                data.fechaNacimiento = "$day/$month/$year"
                repository.register(data)
                    .onSuccess {
                        _uiState.value = RegisterUiState.Success
                    }
                    .onFailure {
                        _uiState.value = RegisterUiState.Error(it.message ?: "Error al registrar")
                    }
            }
        }
    }

    private fun isValidDate(dayStr: String, monthStr: String, yearStr: String): Boolean {
        try {
            val d = dayStr.toInt()
            val m = monthStr.toInt()
            val y = yearStr.toInt()

            if (y < 1900 || y > Calendar.getInstance().get(Calendar.YEAR)) return false
            if (m < 1 || m > 12) return false
            
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, y)
            cal.set(Calendar.MONTH, m - 1)
            val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            
            if (d < 1 || d > maxDay) return false

            val birthDate = Calendar.getInstance()
            birthDate.set(y, m - 1, d)
            if (birthDate.after(Calendar.getInstance())) return false

            return true
        } catch (e: Exception) {
            return false
        }
    }
}
