package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityViewModelTest {

    @After
    fun tearDown() = runBlocking {
        UserRepository.instance.login("maria@gmail.com", "Maria123!")
        Unit
    }

    @Test
    fun changePassword_persistsNewPasswordForCurrentUser() = runBlocking {
        val email = "security_password_test@gmail.com"
        val oldPassword = "Test123!"
        val newPassword = "NewPass123!"

        UserRepository.instance.register(
            email = email,
            password = oldPassword,
            username = "security_password_test",
            fullName = "Security Test",
            birthDate = "1 de enero de 2000",
            bio = "Bio de prueba"
        )

        val viewModel = SecurityViewModel()
        viewModel.onCurrentPasswordChange(oldPassword)
        viewModel.onNewPasswordChange(newPassword)
        viewModel.onConfirmPasswordChange(newPassword)
        viewModel.changePassword()

        assertNotNull(viewModel.successMessage.value)
        assertTrue(UserRepository.instance.login(email, oldPassword).isFailure)
        assertTrue(UserRepository.instance.login(email, newPassword).isSuccess)
    }
}
