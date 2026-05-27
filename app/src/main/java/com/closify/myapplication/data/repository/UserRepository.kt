package com.closify.myapplication.data.repository

import com.closify.myapplication.R
import com.closify.myapplication.domain.model.User
import com.closify.myapplication.domain.model.UserProfile
import kotlinx.coroutines.delay

private data class UserRecord(
    val user: User,
    val password: String
)

class UserRepository {

    companion object {
        val instance = UserRepository()
    }

    private val users = mutableListOf(
        UserRecord(MockClosifyData.currentUser, "Password1!"),
        UserRecord(
            user = User(
                id = "auth_maria",
                email = "maria@gmail.com",
                profile = UserProfile(
                    id = "auth_maria",
                    fullName = "Maria",
                    username = "maria",
                    birthDate = "",
                    bio = "",
                    avatarImageResId = R.drawable.avatar_default,
                    bannerImageResId = R.drawable.banner_default
                )
            ),
            password = "Maria123!"
        ),
        UserRecord(
            user = User(
                id = "auth_juan",
                email = "juan@gmail.com",
                profile = UserProfile(
                    id = "auth_juan",
                    fullName = "Juan",
                    username = "juan",
                    birthDate = "",
                    bio = "",
                    avatarImageResId = R.drawable.avatar_default,
                    bannerImageResId = R.drawable.banner_default
                )
            ),
            password = "Juan123!"
        )
    )

    var currentUserId: String = ""
        private set

    var currentUsername: String = ""
        private set

    fun getCurrentUser(): User? =
        users.firstOrNull { it.user.id == currentUserId }?.user

    fun getUserById(userId: String): User? =
        users.firstOrNull { it.user.id == userId }?.user ?: MockClosifyData.userById(userId)

    suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000)
        val record = users.find { it.user.email == email && it.password == password }
        return if (record != null) {
            currentUserId = record.user.id
            currentUsername = record.user.username
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
        val user = User(
            id = "auth_${users.size + 1}",
            email = email,
            profile = UserProfile(
                id = "auth_${users.size + 1}",
                fullName = username,
                username = username,
                birthDate = "",
                bio = "",
                avatarImageResId = R.drawable.avatar_default,
                bannerImageResId = R.drawable.banner_default
            )
        )
        users.add(UserRecord(user = user, password = password))
        currentUserId = user.id
        currentUsername = user.username
        return Result.success(Unit)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        delay(300)
        return users.none { it.user.username.lowercase() == username.lowercase() }
    }
}
