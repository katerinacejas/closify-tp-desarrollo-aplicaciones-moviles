package com.closify.myapplication.domain.repository

enum class ProfileImageType(val storageName: String) {
    AVATAR("avatar"),
    BANNER("banner")
}

interface ProfileImageRepository {
    suspend fun uploadProfileImage(
        userId: String,
        imageUri: String,
        imageType: ProfileImageType
    ): Result<String>
}
