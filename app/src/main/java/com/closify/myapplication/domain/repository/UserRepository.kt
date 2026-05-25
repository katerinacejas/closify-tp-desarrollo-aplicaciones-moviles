package com.closify.myapplication.domain.repository

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, username: String): Result<Unit>
    suspend fun isUsernameAvailable(username: String): Boolean
}
