package com.closify.myapplication.data.repository

import android.content.Context
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toNotificationEntity
import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.NotificationType
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class NotificationRepository private constructor(context: Context) {

    companion object {
        @Volatile private var _instance: NotificationRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = NotificationRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: NotificationRepository
            get() = _instance ?: error("NotificationRepository.initialize(context) no fue llamado.")
    }

    private val db = AppDatabase.getInstance(context)
    private val notificationDao = db.notificationDao()
    private val userDao = db.userDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncedThisSession = false

    suspend fun getNotifications(userId: String): List<Notification> {
        val entities = notificationDao.getAllByUserId(userId)
        return entities.mapNotNull { entity ->
            val sender = userDao.getById(entity.senderId)?.toDomain()?.toSummary()
            val receiver = userDao.getById(entity.receiverId)?.toDomain()?.toSummary()
            if (sender != null && receiver != null) entity.toDomain(sender, receiver) else null
        }
    }

    suspend fun getUnreadCount(userId: String): Int {
        return notificationDao.getUnreadCount(userId)
    }

    suspend fun syncNotifications(userId: String) {
        if (syncedThisSession) return
        try {
            val snapshot = firestore.collection("users/$userId/notifications").get().await()
            val entities = snapshot.documents.mapNotNull { it.toNotificationEntity() }
            notificationDao.upsertAll(entities)
            syncedThisSession = true
        } catch (e: Exception) {
            android.util.Log.w("NotificationRepository", "syncNotifications failed: ${e.message}")
        }
    }

    suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
        scope.launch {
            try {
                val batch = firestore.batch()
                val snapshot = firestore.collection("users/$userId/notifications")
                    .whereEqualTo("read", false)
                    .get()
                    .await()
                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "read", true)
                }
                batch.commit().await()
            } catch (e: Exception) { }
        }
    }

    suspend fun createPostLikeNotification(post: OutfitPost, sender: UserSummary) {
        if (post.author.id == sender.id) return
        createNotification(
            receiver = post.author,
            sender = sender,
            type = NotificationType.POST_LIKE,
            postId = post.id
        )
    }

    suspend fun createPostCommentNotification(post: OutfitPost, commentId: String, sender: UserSummary) {
        if (post.author.id == sender.id) return
        createNotification(
            receiver = post.author,
            sender = sender,
            type = NotificationType.POST_COMMENT,
            postId = post.id,
            commentId = commentId
        )
    }

    suspend fun createFriendRequestNotification(receiver: UserSummary, sender: UserSummary, requestId: String) {
        createNotification(
            receiver = receiver,
            sender = sender,
            type = NotificationType.FRIEND_REQUEST_RECEIVED,
            friendRequestId = requestId
        )
    }

    suspend fun createFriendRequestAcceptedNotification(receiver: UserSummary, sender: UserSummary, requestId: String) {
        createNotification(
            receiver = receiver,
            sender = sender,
            type = NotificationType.FRIEND_REQUEST_ACCEPTED,
            friendRequestId = requestId
        )
    }

    private suspend fun createNotification(
        receiver: UserSummary,
        sender: UserSummary,
        type: NotificationType,
        postId: String? = null,
        commentId: String? = null,
        friendRequestId: String? = null
    ) {
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            receiver = receiver,
            sender = sender,
            type = type,
            postId = postId,
            commentId = commentId,
            friendRequestId = friendRequestId,
            createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
            )
        )

        notificationDao.upsert(notification.toEntity())
        
        try {
            firestore.collection("users/${receiver.id}/notifications")
                .document(notification.id)
                .set(notification.toFirestoreMap())
                .await()
        } catch (e: Exception) { }
    }

    fun resetSessionSync() { syncedThisSession = false }
}
