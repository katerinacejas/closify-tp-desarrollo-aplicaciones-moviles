package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Notification

class NotificationRepository {

    companion object {
        val instance = NotificationRepository()
    }

    fun getNotifications(userId: String = MockClosifyData.CURRENT_USER_ID): List<Notification> =
        MockClosifyData.notifications
            .filter { it.receiver.id == userId }
            .sortedByDescending { it.createdAt }

    fun getUnreadCount(userId: String = MockClosifyData.CURRENT_USER_ID): Int =
        getNotifications(userId).count { !it.read }
}
