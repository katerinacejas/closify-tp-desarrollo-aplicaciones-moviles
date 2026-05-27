package com.closify.myapplication.domain.model

import androidx.annotation.DrawableRes

data class User(
    val id: String,
    val fullName: String,
    val username: String,
    val email: String? = null,
    val birthDate: String,
    val bio: String,
    @param:DrawableRes val avatarImageResId: Int,
    @param:DrawableRes val bannerImageResId: Int,
    val createdAt: String = ""
) {
    fun toSummary(): UserSummary = UserSummary(
        id = id,
        fullName = fullName,
        username = username,
        profileImageResId = avatarImageResId
    )
}
