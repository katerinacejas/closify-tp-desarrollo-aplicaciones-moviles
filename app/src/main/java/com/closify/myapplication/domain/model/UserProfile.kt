package com.closify.myapplication.domain.model

import androidx.annotation.DrawableRes

data class UserProfile(
    val id: String,
    val fullName: String,
    val username: String,
    val birthDate: String,
    val bio: String,
    @param:DrawableRes val avatarImageResId: Int,
    @param:DrawableRes val bannerImageResId: Int,
    val avatarImageUrl: String? = null,
    val bannerImageUrl: String? = null
) {
    val name: String
        get() = fullName

    val profileImageResId: Int
        get() = avatarImageResId

    fun toSummary(): UserSummary = UserSummary(
        id = id,
        fullName = fullName,
        username = username,
        profileImageResId = avatarImageResId,
        profileImageUrl = avatarImageUrl
    )
}
