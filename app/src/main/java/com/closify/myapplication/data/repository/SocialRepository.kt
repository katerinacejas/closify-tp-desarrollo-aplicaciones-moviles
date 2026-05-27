package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary

class SocialRepository {

    companion object {
        val instance = SocialRepository()
    }

    fun getFriends(userId: String = MockClosifyData.CURRENT_USER_ID): List<UserSummary> =
        MockClosifyData.friendships
            .filter { it.userA.id == userId || it.userB.id == userId }
            .map { if (it.userA.id == userId) it.userB else it.userA }

    fun getFriendships(userId: String = MockClosifyData.CURRENT_USER_ID): List<Friendship> =
        MockClosifyData.friendships.filter { it.userA.id == userId || it.userB.id == userId }

    fun getFriendRequests(userId: String = MockClosifyData.CURRENT_USER_ID): List<FriendRequest> =
        MockClosifyData.friendRequests.filter { it.sender.id == userId || it.receiver.id == userId }

    fun getPendingReceivedRequests(userId: String = MockClosifyData.CURRENT_USER_ID): List<FriendRequest> =
        getFriendRequests(userId).filter {
            it.receiver.id == userId && it.status == FriendRequestStatus.PENDING
        }

    fun getFriendsFeed(userId: String = MockClosifyData.CURRENT_USER_ID): List<OutfitPost> {
        val friendIds = getFriends(userId).map { it.id }.toSet()
        return MockClosifyData.outfitPosts
            .filter { it.author.id in friendIds }
            .sortedByDescending { it.createdAt }
    }
}
