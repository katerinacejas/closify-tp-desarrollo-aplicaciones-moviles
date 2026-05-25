package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.repository.UserRepository
import kotlinx.coroutines.delay

// Modelo interno del repositorio — no se expone fuera de esta capa
private data class UserRecord(
    val email: String,
    val password: String,
    val username: String
)

class FakeUserRepository : UserRepository {

    // Usuarios precargados para poder hacer login sin registrarse
    // TODO: reemplazar por Firebase Auth
    private val users = mutableListOf(
        UserRecord("test@closify.com", "Password1!", "closify"),
        UserRecord("maria@gmail.com",  "Maria123!",  "maria"),
        UserRecord("juan@gmail.com",   "Juan123!",   "juan")
    )

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000)
        return if (users.any { it.email == email && it.password == password }) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales incorrectas."))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        delay(1000)
        users.add(UserRecord(email, password, username))
        return Result.success(Unit)
    }

    override suspend fun isUsernameAvailable(username: String): Boolean {
        delay(300)
        return users.none { it.username.lowercase() == username.lowercase() }
    }
}
