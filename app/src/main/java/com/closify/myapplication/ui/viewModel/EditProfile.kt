package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditProfileUiState(
    val fullName: String = "",
    val username: String = "",
    val bio: String = "",
    val birthDate: String = "",
    val fullNameError: String? = null,
    val usernameError: String? = null,
    val bioError: String? = null,
    val generalError: String? = null,
    val saved: Boolean = false
)

class EditProfileViewModel(
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null, saved = false) }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = normalizeUsername(value), usernameError = null, saved = false) }
    }

    fun onBioChange(value: String) {
        if (value.length <= 140) {
            _uiState.update { it.copy(bio = value, bioError = null, saved = false) }
        }
    }

    fun onBirthDateChange(value: String) {
        _uiState.update { it.copy(birthDate = value, saved = false) }
    }

    fun saveChanges() {
        val state = _uiState.value
        val fullNameError = if (state.fullName.isBlank()) "El nombre no puede estar vacio." else null
        val usernameError = if (state.username.isBlank() || state.username == "@") "El usuario no puede estar vacio." else null
        val bioError = if (state.bio.length > 140) "La biografia no puede superar los 140 caracteres." else null

        if (fullNameError != null || usernameError != null || bioError != null) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    usernameError = usernameError,
                    bioError = bioError
                )
            }
            return
        }

        userRepository.updateCurrentUserProfile(
            fullName = state.fullName,
            username = state.username,
            birthDate = state.birthDate,
            bio = state.bio
        ).onSuccess {
            analyticsTracker.track(AnalyticsEvents.profileUpdated())
            _uiState.update { it.copy(saved = true, generalError = null) }
        }.onFailure { error ->
            analyticsTracker.track(AnalyticsEvents.profileUpdateFailed(error.message))
            _uiState.update { it.copy(generalError = error.message, saved = false) }
        }
    }

    private fun loadCurrentProfile() {
        val profile = userRepository.getCurrentUserOrDefault().profile
        _uiState.value = EditProfileUiState(
            fullName = profile.fullName,
            username = profile.username,
            bio = profile.bio,
            birthDate = profile.birthDate
        )
    }

    private fun normalizeUsername(value: String): String {
        val trimmed = value.trim()
        return if (trimmed.startsWith("@")) trimmed else "@$trimmed"
    }
}
