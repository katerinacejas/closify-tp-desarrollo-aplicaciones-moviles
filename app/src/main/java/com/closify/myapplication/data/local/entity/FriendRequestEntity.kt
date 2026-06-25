package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val status: String, // PENDING, ACCEPTED, REJECTED
    val createdAt: String,
    val respondedAt: String? = null
)
