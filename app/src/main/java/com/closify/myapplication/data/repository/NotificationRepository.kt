package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.NotificationType
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary

class NotificationRepository {

    companion object {
        val instance = NotificationRepository()
    }

    fun getNotifications(userId: String = MockClosifyData.CURRENT_USER_ID): List<Notification> =
        MockClosifyData.notifications
            .filter { it.receiver.id == userId }
            .sortedBy { it.createdAt.toNotificationOrder() }

    fun getUnreadCount(userId: String = MockClosifyData.CURRENT_USER_ID): Int =
        getNotifications(userId).count { !it.read }

    fun markAllAsRead(userId: String = MockClosifyData.CURRENT_USER_ID) {
        MockClosifyData.markNotificationsAsRead(userId)
    }

    fun createPostLikeNotification(post: OutfitPost, sender: UserSummary): Notification? {
        if (post.author.id == sender.id) return null

        return MockClosifyData.addNotification(
            receiver = post.author,
            sender = sender,
            type = NotificationType.POST_LIKE,
            postId = post.id
        )
    }

    fun createPostCommentNotification(
        post: OutfitPost,
        commentId: String,
        sender: UserSummary
    ): Notification? {
        if (post.author.id == sender.id) return null

        return MockClosifyData.addNotification(
            receiver = post.author,
            sender = sender,
            type = NotificationType.POST_COMMENT,
            postId = post.id,
            commentId = commentId
        )
    }

    private fun String.toNotificationOrder(): Int {
        if (this == "ahora") return 0
        val parts = split(" ")
        if (parts.size >= 2 && parts.first() == "hace") {
            val amount = parts.getOrNull(1)?.toIntOrNull() ?: return Int.MAX_VALUE
            return when {
                contains("minuto") -> amount
                contains("hora") -> amount * 60
                contains("dia") -> amount * 60 * 24
                else -> Int.MAX_VALUE
            }
        }
        return Int.MAX_VALUE
    }
}
