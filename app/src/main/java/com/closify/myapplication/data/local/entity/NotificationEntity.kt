package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val receiverId: String,
    val senderId: String,
    val type: String, // POST_LIKE, POST_COMMENT, etc.
    val postId: String? = null,
    val commentId: String? = null,
    val friendRequestId: String? = null,
    val createdAt: String,
    val read: Boolean = false
)
