package com.closify.myapplication.data.local.entity

import androidx.room.Entity

@Entity(tableName = "likes", primaryKeys = ["postId", "userId"])
data class LikeEntity(
    val postId: String,
    val userId: String,
    val id: String,
    val createdAt: String
)
