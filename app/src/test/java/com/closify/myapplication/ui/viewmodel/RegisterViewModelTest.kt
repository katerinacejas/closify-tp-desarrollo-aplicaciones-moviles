package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterViewModelTest {

    @After
    fun tearDown() = runBlocking {
        UserRepository.instance.login("maria@gmail.com", "Maria123!")
        Unit
    }

    @Test
    fun register_persistsProfileData() = runBlocking {
        val username = "test_register_profile"

        UserRepository.instance.register(
            email = "test_register_profile@gmail.com",
            password = "Test123!",
            username = username,
            fullName = "Sofia Registro",
            birthDate = "14 de abril de 2001",
            bio = "Bio cargada desde el registro."
        )

        val state = ProfileViewModel().uiState.value

        assertEquals("Sofia Registro", state.name)
        assertEquals("@$username", state.username)
        assertEquals("14 de abril de 2001", state.birthDate)
        assertEquals("Bio cargada desde el registro.", state.bio)
    }
}
