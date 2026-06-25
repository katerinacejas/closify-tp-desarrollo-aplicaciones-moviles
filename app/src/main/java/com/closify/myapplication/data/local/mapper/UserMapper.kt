package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.R
import com.closify.myapplication.data.local.entity.UserEntity
import com.closify.myapplication.domain.model.User
import com.closify.myapplication.domain.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    profile = UserProfile(
        id = id,
        fullName = fullName,
        username = username,
        birthDate = birthDate,
        bio = bio,
        avatarImageResId = R.drawable.avatar_default,
        bannerImageResId = R.drawable.banner_default,
        avatarImageUrl = avatarImageUrl.ifBlank { null },
        bannerImageUrl = bannerImageUrl.ifBlank { null }
    ),
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    fullName = profile.fullName,
    username = profile.username,
    birthDate = profile.birthDate,
    bio = profile.bio,
    avatarImageUrl = profile.avatarImageUrl.orEmpty(),
    bannerImageUrl = profile.bannerImageUrl.orEmpty(),
    createdAt = createdAt
)

fun User.toFirestoreMap(): Map<String, Any> = mapOf(
    "email" to email,
    "fullName" to profile.fullName,
    "username" to profile.username,
    "birthDate" to profile.birthDate,
    "bio" to profile.bio,
    "avatarImageUrl" to profile.avatarImageUrl.orEmpty(),
    "bannerImageUrl" to profile.bannerImageUrl.orEmpty(),
    "createdAt" to createdAt
)

fun DocumentSnapshot.toUserEntity(): UserEntity? {
    return try {
        UserEntity(
            id = id,
            email = getString("email") ?: "",
            fullName = getString("fullName") ?: "",
            username = getString("username") ?: "",
            birthDate = getString("birthDate") ?: "",
            bio = getString("bio") ?: "",
            avatarImageUrl = getString("avatarImageUrl") ?: "",
            bannerImageUrl = getString("bannerImageUrl") ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}
