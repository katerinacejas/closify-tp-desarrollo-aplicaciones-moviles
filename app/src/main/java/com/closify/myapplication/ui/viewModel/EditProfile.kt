package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class EditProfileViewModel : ViewModel() {
    // Mock de usuario inicial
    private val _user = MutableStateFlow(
        User(
            id = "1",
            name = "Katerina",
            username = "katerina_closify",
            bio = "Amante de la moda y el orden.",
            birthdate = LocalDate.of(2000, 5, 15)
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    private val _name = MutableStateFlow(_user.value.name)
    val name: StateFlow<String> = _name.asStateFlow()

    private val _username = MutableStateFlow(_user.value.username)
    val username: StateFlow<String> = _username.asStateFlow()

    private val _bio = MutableStateFlow(_user.value.bio)
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _birthdate = MutableStateFlow(_user.value.birthdate)
    val birthdate: StateFlow<LocalDate?> = _birthdate.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onUsernameChange(newUsername: String) {
        // Aseguramos que empiece con @ o lo manejamos en la UI
        _username.value = newUsername
    }

    fun onBioChange(newBio: String) {
        if (newBio.length <= 140) {
            _bio.value = newBio
        }
    }

    fun onBirthdateChange(newDate: LocalDate) {
        _birthdate.value = newDate
    }

    fun saveChanges() {
        // Aquí impactaría en el repositorio/base de datos
        _user.value = _user.value.copy(
            name = _name.value,
            username = _username.value,
            bio = _bio.value,
            birthdate = _birthdate.value
        )
    }
}
