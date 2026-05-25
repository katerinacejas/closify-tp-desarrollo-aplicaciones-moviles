package com.closify.myapplication.data.repository

import kotlinx.coroutines.delay

private data class UserRecord(
    val email: String,
    val password: String,
    val username: String
)

class UserRepository {

    companion object {
        val instance = UserRepository()
    }

    // Usuarios precargados para poder hacer login sin registrarse
    // TODO: reemplazar por Firebase Auth
    private val users = mutableListOf(
        UserRecord("test@closify.com", "Password1!", "closify"),
        UserRecord("maria@gmail.com",  "Maria123!",  "maria"),
        UserRecord("juan@gmail.com",   "Juan123!",   "juan")
    )

    suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000)
        return if (users.any { it.email == email && it.password == password }) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales incorrectas."))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        delay(1000)
        users.add(UserRecord(email, password, username))
        return Result.success(Unit)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        delay(300)
        return users.none { it.username.lowercase() == username.lowercase() }
    }
}
