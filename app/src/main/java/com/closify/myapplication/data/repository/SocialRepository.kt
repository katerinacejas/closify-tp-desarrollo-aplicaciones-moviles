package com.closify.myapplication.data.repository

import android.content.Context
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toFriendRequestEntity
import com.closify.myapplication.data.local.mapper.toFriendshipEntity
import com.closify.myapplication.data.local.mapper.toUserEntity
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.UserSummary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class SocialRepository private constructor(
    context: Context,
    private val notificationRepository: NotificationRepository = NotificationRepository.instance
) {

    companion object {
        @Volatile private var _instance: SocialRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = SocialRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: SocialRepository
            get() = _instance ?: error("SocialRepository.initialize(context) no fue llamado.")
    }

    private val db = AppDatabase.getInstance(context)
    private val friendshipDao = db.friendshipDao()
    private val requestDao = db.friendRequestDao()
    private val userDao = db.userDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncedThisSession = false

    fun observeFriends(userId: String): Flow<List<UserSummary>> {
        return friendshipDao.observeByUserId(userId).map { entities ->
            entities.mapNotNull { entity ->
                val friendId = if (entity.userAId == userId) entity.userBId else entity.userAId
                userDao.getById(friendId)?.toDomain()?.toSummary()
            }
        }
    }

    suspend fun getFriends(userId: String): List<UserSummary> {
        return friendshipDao.getAllByUserId(userId).mapNotNull { entity ->
            val friendId = if (entity.userAId == userId) entity.userBId else entity.userAId
            userDao.getById(friendId)?.toDomain()?.toSummary()
        }
    }

    suspend fun syncSocialData(userId: String) {
        if (syncedThisSession) return
        try {
            // Sync friendships
            val friendshipSnapshot = firestore.collection("friendships")
                .whereArrayContains("userIds", userId)
                .get()
                .await()
            val friendshipEntities = friendshipSnapshot.documents.mapNotNull { it.toFriendshipEntity() }
            friendshipDao.upsertAll(friendshipEntities)

            // Sync friend requests
            val incomingRequests = firestore.collection("friend_requests")
                .whereEqualTo("receiverId", userId)
                .get()
                .await()
            val outgoingRequests = firestore.collection("friend_requests")
                .whereEqualTo("senderId", userId)
                .get()
                .await()
            
            val requestEntities = (incomingRequests.documents + outgoingRequests.documents)
                .mapNotNull { it.toFriendRequestEntity() }
            requestDao.upsertAll(requestEntities)
            syncedThisSession = true
        } catch (e: Exception) {
            android.util.Log.w("SocialRepository", "syncSocialData failed: ${e.message}")
        }
    }

    suspend fun isFriend(userId: String, otherUserId: String): Boolean {
        val friends = getFriends(userId)
        return friends.any { it.id == otherUserId }
    }

    suspend fun sendFriendRequest(senderId: String, receiverId: String): Result<Unit> {
        return try {
            val createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
            )
            val senderSummary = userDao.getById(senderId)?.toDomain()?.toSummary() ?: return Result.failure(Exception("Sender not found"))
            val receiverSummary = userDao.getById(receiverId)?.toDomain()?.toSummary() ?: return Result.failure(Exception("Receiver not found"))
            
            val request = FriendRequest(
                id = UUID.randomUUID().toString(),
                sender = senderSummary,
                receiver = receiverSummary,
                status = FriendRequestStatus.PENDING,
                createdAt = createdAt
            )
            
            requestDao.upsert(request.toEntity())
            firestore.collection("friend_requests").document(request.id).set(request.toFirestoreMap()).await()
            
            // Create notification
            notificationRepository.createFriendRequestNotification(receiverSummary, senderSummary, request.id)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondToFriendRequest(requestId: String, accepted: Boolean): Result<Unit> {
        return try {
            val entity = requestDao.getById(requestId) ?: return Result.failure(Exception("Request not found"))
            val status = if (accepted) FriendRequestStatus.ACCEPTED else FriendRequestStatus.REJECTED
            val respondedAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
            )
            
            val updatedEntity = entity.copy(status = status.name, respondedAt = respondedAt)
            requestDao.upsert(updatedEntity)
            
            firestore.collection("friend_requests").document(requestId)
                .update("status", status.name, "respondedAt", respondedAt)
                .await()

            if (accepted) {
                createFriendship(entity.senderId, entity.receiverId)
                
                // Create acceptance notification
                val senderSummary = userDao.getById(entity.senderId)?.toDomain()?.toSummary()
                val receiverSummary = userDao.getById(entity.receiverId)?.toDomain()?.toSummary()
                if (senderSummary != null && receiverSummary != null) {
                    notificationRepository.createFriendRequestAcceptedNotification(senderSummary, receiverSummary, requestId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createFriendship(userAId: String, userBId: String) {
        val createdAt = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
        )
        val friendship = Friendship(
            id = UUID.randomUUID().toString(),
            userA = userDao.getById(userAId)?.toDomain()?.toSummary() ?: return,
            userB = userDao.getById(userBId)?.toDomain()?.toSummary() ?: return,
            createdAt = createdAt
        )
        
        friendshipDao.upsert(friendship.toEntity())
        
        // In Firestore we store with an extra array for easier querying
        val firestoreMap = friendship.toFirestoreMap().toMutableMap()
        firestoreMap["userIds"] = listOf(userAId, userBId)
        
        firestore.collection("friendships").document(friendship.id).set(firestoreMap).await()
    }

    suspend fun removeFriend(userId: String, friendId: String) {
        friendshipDao.deleteFriendship(userId, friendId)
        // Note: Removing from Firestore would require finding the doc by userIds
        val snapshot = firestore.collection("friendships")
            .whereArrayContains("userIds", userId)
            .get()
            .await()
        
        val docToDelete = snapshot.documents.find { doc ->
            val userIds = doc.get("userIds") as? List<*>
            userIds?.contains(friendId) == true
        }
        
        docToDelete?.reference?.delete()?.await()
    }

    fun observePendingReceivedRequests(userId: String): Flow<List<FriendRequest>> {
        return requestDao.observePendingIncoming(userId).map { entities ->
            entities.mapNotNull { entity ->
                val sender = userDao.getById(entity.senderId)?.toDomain()?.toSummary()
                val receiver = userDao.getById(entity.receiverId)?.toDomain()?.toSummary()
                if (sender != null && receiver != null) entity.toDomain(sender, receiver) else null
            }
        }
    }
    
    suspend fun getPendingOutgoingFriendRequest(senderId: String, receiverId: String): FriendRequest? {
        val entities = requestDao.getAllByUserId(senderId)
        val entity = entities.find { it.senderId == senderId && it.receiverId == receiverId && it.status == FriendRequestStatus.PENDING.name }
        return entity?.let {
            val sender = userDao.getById(it.senderId)?.toDomain()?.toSummary()
            val receiver = userDao.getById(it.receiverId)?.toDomain()?.toSummary()
            if (sender != null && receiver != null) it.toDomain(sender, receiver) else null
        }
    }

    suspend fun getPendingIncomingFriendRequest(receiverId: String, senderId: String): FriendRequest? {
        val entities = requestDao.getAllByUserId(receiverId)
        val entity = entities.find { it.senderId == senderId && it.receiverId == receiverId && it.status == FriendRequestStatus.PENDING.name }
        return entity?.let {
            val sender = userDao.getById(it.senderId)?.toDomain()?.toSummary()
            val receiver = userDao.getById(it.receiverId)?.toDomain()?.toSummary()
            if (sender != null && receiver != null) it.toDomain(sender, receiver) else null
        }
    }

    suspend fun getFriendRequest(requestId: String): FriendRequest? {
        val entity = requestDao.getById(requestId) ?: return null
        val sender = userDao.getById(entity.senderId)?.toDomain()?.toSummary() ?: return null
        val receiver = userDao.getById(entity.receiverId)?.toDomain()?.toSummary() ?: return null
        return entity.toDomain(sender, receiver)
    }

    suspend fun getAllUserSummaries(excludeUserId: String): List<UserSummary> {
        return try {
            firestore.collection("users")
                .get()
                .await()
                .documents
                .mapNotNull { it.toUserEntity()?.toDomain()?.toSummary() }
                .filter { it.id != excludeUserId }
        } catch (e: Exception) {
            android.util.Log.w("SocialRepository", "getAllUserSummaries failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchUserSummariesByName(query: String, currentUserId: String): List<UserSummary> {
        val cleanQuery = query.trim().removePrefix("@").lowercase()
        if (cleanQuery.isBlank()) return emptyList()

        return try {
            firestore.collection("users")
                .get()
                .await()
                .documents
                .mapNotNull { it.toUserEntity()?.toDomain()?.toSummary() }
                .filter { it.id != currentUserId }
                .filter {
                    it.fullName.lowercase().contains(cleanQuery) ||
                    it.username.lowercase().contains(cleanQuery)
                }
        } catch (e: Exception) {
            android.util.Log.w("SocialRepository", "searchUserSummariesByName failed: ${e.message}")
            emptyList()
        }
    }

    fun resetSessionSync() { syncedThisSession = false }
}
