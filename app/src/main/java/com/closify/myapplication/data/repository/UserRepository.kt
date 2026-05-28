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
}
