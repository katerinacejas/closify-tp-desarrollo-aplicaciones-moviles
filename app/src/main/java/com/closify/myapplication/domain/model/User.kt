package com.closify.myapplication.domain.model

data class User(
    val id: String,
    val email: String,
    val profile: UserProfile,
    val createdAt: String = ""
) {
    val username: String
        get() = profile.username

    fun toSummary(): UserSummary = profile.toSummary()
}
