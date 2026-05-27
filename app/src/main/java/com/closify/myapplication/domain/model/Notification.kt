package com.closify.myapplication.domain.model

data class Notification(
    val id: String,
    val receiver: UserSummary,
    val sender: UserSummary,
    val type: NotificationType,
    val postId: String? = null,
    val commentId: String? = null,
    val friendRequestId: String? = null,
    val createdAt: String,
    val read: Boolean = false
)
