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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    fun observeNotifications(userId: String): Flow<List<Notification>> =
        notificationDao.observeByUserId(userId).map { entities ->
            entities.mapNotNull { entity ->
                // NOTA: Esto es ineficiente en un Flow. En producción se usaría un JOIN o Room Relation.
                // Por ahora, como es para migración, lo dejamos asíncrono manual.
                // Pero en un Flow .map no podemos llamar a suspend functions fácilmente sin bloquear.
                // Usaremos MockClosifyData como fallback para los summaries en el Flow si no están en cache local.
                val sender = MockClosifyData.userById(entity.senderId)?.toSummary()
                val receiver = MockClosifyData.userById(entity.receiverId)?.toSummary()
                if (sender != null && receiver != null) entity.toDomain(sender, receiver) else null
            }
        }

    fun observeUnreadCount(userId: String): Flow<Int> =
        notificationDao.observeUnreadCount(userId)

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
        try {
            val snapshot = firestore.collection("users/$userId/notifications").get().await()
            val entities = snapshot.documents.mapNotNull { it.toNotificationEntity() }
            notificationDao.upsertAll(entities)
        } catch (e: Exception) {
            // Offline
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
            createdAt = "ahora"
        )

        notificationDao.upsert(notification.toEntity())
        
        try {
            firestore.collection("users/${receiver.id}/notifications")
                .document(notification.id)
                .set(notification.toFirestoreMap())
                .await()
        } catch (e: Exception) { }
    }
}
