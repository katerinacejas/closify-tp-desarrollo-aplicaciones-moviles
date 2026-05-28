package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.User
import kotlinx.coroutines.delay

class UserRepository {

    companion object {
        val instance = UserRepository()
    }

    var currentUserId: String = ""
        private set

    var currentUsername: String = ""
        private set

    fun getCurrentUser(): User? =
        MockClosifyData.authUserById(currentUserId) ?: MockClosifyData.userById(currentUserId)

    fun getCurrentUserOrDefault(): User =
        getCurrentUser() ?: MockClosifyData.currentUser

    fun getUserById(userId: String): User? =
        MockClosifyData.authUserById(userId) ?: MockClosifyData.userById(userId)

    suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000)
        val user = MockClosifyData.findAuthUser(email, password)
        return if (user != null) {
            currentUserId = user.id
            currentUsername = user.username
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales incorrectas."))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        fullName: String,
        birthDate: String,
        bio: String
    ): Result<Unit> {
        delay(1000)
        val user = MockClosifyData.registerAuthUser(
            email = email,
            password = password,
            username = username,
            fullName = fullName,
            birthDate = birthDate,
            bio = bio
        )
        currentUserId = user.id
        currentUsername = user.username
        return Result.success(Unit)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        delay(300)
        return MockClosifyData.isUsernameAvailable(username)
    }

    fun updateCurrentUserProfile(
        fullName: String,
        username: String,
        birthDate: String,
        bio: String
    ): Result<Unit> {
        val updatedUser = MockClosifyData.updateUserProfile(
            userId = currentUserId,
            fullName = fullName,
            username = username,
            birthDate = birthDate,
            bio = bio
        ) ?: return Result.failure(Exception("No se pudo actualizar el perfil."))

        currentUsername = updatedUser.username
        return Result.success(Unit)
    }

    fun changeCurrentUserPassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        if (currentUserId.isBlank()) {
            return Result.failure(Exception("No hay un usuario logueado."))
        }

        if (!MockClosifyData.isCurrentPassword(currentUserId, currentPassword)) {
            return Result.failure(Exception("La contraseña actual no es correcta."))
        }

        val updated = MockClosifyData.updateAuthUserPassword(currentUserId, newPassword)
        return if (updated) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("No se pudo actualizar la contraseña."))
        }
    }
}
