package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditProfileViewModel : ViewModel() {
    
    // Usamos UserProfile directamente para evitar el conflicto con User
    private val _profile = MutableStateFlow(
        UserProfile(
            id = "1",
            fullName = "Katerina",
            username = "katerina_closify",
            bio = "Amante de la moda y el orden.",
            birthDate = "15/05/2000",
            avatarImageResId = R.drawable.avatar_default,
            bannerImageResId = R.drawable.banner_default
        )
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    // Estados para los campos editables, usando los nombres de UserProfile
    private val _fullName = MutableStateFlow(_profile.value.fullName)
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _username = MutableStateFlow(_profile.value.username)
    val username: StateFlow<String> = _username.asStateFlow()

    private val _bio = MutableStateFlow(_profile.value.bio)
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _birthDate = MutableStateFlow(_profile.value.birthDate)
    val birthDate: StateFlow<String> = _birthDate.asStateFlow()

    fun onNameChange(newName: String) {
        _fullName.value = newName
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onBioChange(newBio: String) {
        if (newBio.length <= 140) {
            _bio.value = newBio
        }
    }

    fun onBirthDateChange(newDate: String) {
        _birthDate.value = newDate
    }

    fun saveChanges() {
        _profile.value = _profile.value.copy(
            fullName = _fullName.value,
            username = _username.value,
            bio = _bio.value,
            birthDate = _birthDate.value
        )
    }
}
