package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val fullName: String,
    val username: String,
    val birthDate: String,
    val bio: String,
    val avatarImageUrl: String = "",
    val bannerImageUrl: String = "",
    val createdAt: String
)
