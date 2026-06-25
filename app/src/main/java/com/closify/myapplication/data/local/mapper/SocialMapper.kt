package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.FriendRequestEntity
import com.closify.myapplication.data.local.entity.FriendshipEntity
import com.closify.myapplication.data.local.entity.NotificationEntity
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.NotificationType
import com.closify.myapplication.domain.model.UserSummary
import com.google.firebase.firestore.DocumentSnapshot

// Friendship Mappers
fun FriendshipEntity.toDomain(userA: UserSummary, userB: UserSummary): Friendship = Friendship(
    id = id,
    userA = userA,
    userB = userB,
    createdAt = createdAt
)

fun Friendship.toEntity(): FriendshipEntity = FriendshipEntity(
    id = id,
    userAId = userA.id,
    userBId = userB.id,
    createdAt = createdAt
)

fun Friendship.toFirestoreMap(): Map<String, Any> = mapOf(
    "userAId" to userA.id,
    "userBId" to userB.id,
    "userIds" to listOf(userA.id, userB.id),
    "createdAt" to createdAt
)

fun DocumentSnapshot.toFriendshipEntity(): FriendshipEntity? {
    return try {
        FriendshipEntity(
            id = id,
            userAId = getString("userAId") ?: "",
            userBId = getString("userBId") ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}

// FriendRequest Mappers
fun FriendRequestEntity.toDomain(sender: UserSummary, receiver: UserSummary): FriendRequest = FriendRequest(
    id = id,
    sender = sender,
    receiver = receiver,
    status = FriendRequestStatus.valueOf(status),
    createdAt = createdAt,
    respondedAt = respondedAt
)

fun FriendRequest.toEntity(): FriendRequestEntity = FriendRequestEntity(
    id = id,
    senderId = sender.id,
    receiverId = receiver.id,
    status = status.name,
    createdAt = createdAt,
    respondedAt = respondedAt
)

fun FriendRequest.toFirestoreMap(): Map<String, Any> = mapOf(
    "senderId" to sender.id,
    "receiverId" to receiver.id,
    "status" to status.name,
    "createdAt" to createdAt,
    "respondedAt" to (respondedAt ?: "")
)

fun DocumentSnapshot.toFriendRequestEntity(): FriendRequestEntity? {
    return try {
        FriendRequestEntity(
            id = id,
            senderId = getString("senderId") ?: "",
            receiverId = getString("receiverId") ?: "",
            status = getString("status") ?: FriendRequestStatus.PENDING.name,
            createdAt = getString("createdAt") ?: "",
            respondedAt = getString("respondedAt")?.ifEmpty { null }
        )
    } catch (e: Exception) { null }
}

// Notification Mappers
fun NotificationEntity.toDomain(sender: UserSummary, receiver: UserSummary): Notification = Notification(
    id = id,
    sender = sender,
    receiver = receiver,
    type = NotificationType.valueOf(type),
    postId = postId,
    commentId = commentId,
    friendRequestId = friendRequestId,
    createdAt = createdAt,
    read = read
)

fun Notification.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    senderId = sender.id,
    receiverId = receiver.id,
    type = type.name,
    postId = postId,
    commentId = commentId,
    friendRequestId = friendRequestId,
    createdAt = createdAt,
    read = read
)

fun Notification.toFirestoreMap(): Map<String, Any> = mapOf(
    "senderId" to sender.id,
    "receiverId" to receiver.id,
    "type" to type.name,
    "postId" to (postId ?: ""),
    "commentId" to (commentId ?: ""),
    "friendRequestId" to (friendRequestId ?: ""),
    "createdAt" to createdAt,
    "read" to read
)

fun DocumentSnapshot.toNotificationEntity(): NotificationEntity? {
    return try {
        NotificationEntity(
            id = id,
            senderId = getString("senderId") ?: "",
            receiverId = getString("receiverId") ?: "",
            type = getString("type") ?: NotificationType.POST_LIKE.name,
            postId = getString("postId")?.ifEmpty { null },
            commentId = getString("commentId")?.ifEmpty { null },
            friendRequestId = getString("friendRequestId")?.ifEmpty { null },
            createdAt = getString("createdAt") ?: "",
            read = getBoolean("read") ?: false
        )
    } catch (e: Exception) { null }
}
