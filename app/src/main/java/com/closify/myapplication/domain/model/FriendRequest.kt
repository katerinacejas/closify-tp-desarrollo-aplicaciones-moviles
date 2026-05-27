package com.closify.myapplication.domain.model

data class FriendRequest(
    val id: String,
    val sender: UserSummary,
    val receiver: UserSummary,
    val status: FriendRequestStatus,
    val createdAt: String,
    val respondedAt: String? = null
)
