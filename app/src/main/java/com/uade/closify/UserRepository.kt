package com.uade.closify

import kotlinx.coroutines.delay

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(userData: com.uade.closify.register.RegisterData): Result<Unit>
    suspend fun isUsernameAvailable(username: String): Boolean
}

class UserRepositoryImpl : UserRepository {
    private val existingUsers = listOf("admin", "aylen", "user123")

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(2000)
        return if (email == "test@example.com" && password == "password123") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales incorrectas"))
        }
    }

    override suspend fun register(userData: com.uade.closify.register.RegisterData): Result<Unit> {
        delay(2000)
        return Result.success(Unit)
    }

    override suspend fun isUsernameAvailable(username: String): Boolean {
        delay(500)
        return !existingUsers.contains(username.lowercase())
    }
}
