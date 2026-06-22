package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class EditProfileViewModelTest {

    @After
    fun tearDown() = runBlocking {
        UserRepository.instance.login("maria@gmail.com", "Maria123!")
        Unit
    }

    @Test
    fun saveChanges_persistsCurrentUserProfileData() = runBlocking {
        UserRepository.instance.register(
            email = "edit_profile_test@gmail.com",
            password = "Test123!",
            username = "edit_profile_test",
            fullName = "Nombre Inicial",
            birthDate = "1 de enero de 2000",
            bio = "Bio inicial"
        )

        val editProfileViewModel = EditProfileViewModel()
        editProfileViewModel.onNameChange("Nombre Editado")
        editProfileViewModel.onUsernameChange("usuario_editado")
        editProfileViewModel.onBioChange("Bio editada desde configuracion.")
        editProfileViewModel.onBirthDateChange("9 de febrero de 2002")
        editProfileViewModel.saveChanges()

        val profileState = ProfileViewModel().uiState.value

        assertEquals("Nombre Editado", profileState.name)
        assertEquals("@usuario_editado", profileState.username)
        assertEquals("Bio editada desde configuracion.", profileState.bio)
        assertEquals("9 de febrero de 2002", profileState.birthDate)
    }
}
