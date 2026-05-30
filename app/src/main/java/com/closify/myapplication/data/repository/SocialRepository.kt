package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.UserSummary

class SocialRepository {

    companion object {
        val instance = SocialRepository()
    }

    fun getFriends(userId: String = MockClosifyData.CURRENT_USER_ID): List<UserSummary> =
        MockClosifyData.friendIds(userId)
            .mapNotNull { MockClosifyData.userById(it)?.toSummary() }

    fun getAllUserSummaries(userId: String = MockClosifyData.CURRENT_USER_ID): List<UserSummary> =
        MockClosifyData.users
            .filterNot { it.id == userId }
            .map { it.toSummary() }

    fun searchUserSummariesByName(
        query: String,
        userId: String = MockClosifyData.CURRENT_USER_ID
    ): List<UserSummary> {
        val cleanQuery = query.trim().removePrefix("@")
        if (cleanQuery.isBlank()) return emptyList()

        return getAllUserSummaries(userId)
            .filter { it.name.startsWith(cleanQuery, ignoreCase = true) }
    }

    fun isFriend(userId: String, otherUserId: String): Boolean =
        MockClosifyData.isFriend(userId, otherUserId)

    fun addFriend(userId: String = MockClosifyData.CURRENT_USER_ID, friendId: String) {
        MockClosifyData.addFriend(userId, friendId)
    }

    fun removeFriend(userId: String = MockClosifyData.CURRENT_USER_ID, friendId: String) {
        MockClosifyData.removeFriend(userId, friendId)
    }

    fun sendFriendRequest(senderId: String, receiverId: String): FriendRequest? =
        MockClosifyData.sendFriendRequest(senderId, receiverId)

    fun getPendingOutgoingFriendRequest(senderId: String, receiverId: String): FriendRequest? =
        MockClosifyData.pendingOutgoingFriendRequest(senderId, receiverId)

    fun getPendingIncomingFriendRequest(receiverId: String, senderId: String): FriendRequest? =
        MockClosifyData.pendingIncomingFriendRequest(receiverId, senderId)

    fun respondToFriendRequest(requestId: String, accepted: Boolean): FriendRequest? =
        MockClosifyData.respondToFriendRequest(requestId, accepted)

    fun getFriendRequest(requestId: String): FriendRequest? =
        MockClosifyData.friendRequestById(requestId)

    fun getFriendships(userId: String = MockClosifyData.CURRENT_USER_ID): List<Friendship> =
        MockClosifyData.friendships.filter { it.userA.id == userId || it.userB.id == userId }

    fun getFriendRequests(userId: String = MockClosifyData.CURRENT_USER_ID): List<FriendRequest> =
        MockClosifyData.friendRequests.filter { it.sender.id == userId || it.receiver.id == userId }

    fun getPendingReceivedRequests(userId: String = MockClosifyData.CURRENT_USER_ID): List<FriendRequest> =
        getFriendRequests(userId).filter {
            it.receiver.id == userId && it.status == FriendRequestStatus.PENDING
        }
}
