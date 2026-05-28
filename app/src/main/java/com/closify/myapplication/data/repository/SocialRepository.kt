package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserProfile
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

    fun getUserProfile(userId: String): UserProfile? =
        MockClosifyData.userById(userId)?.profile

    fun isFriend(userId: String, otherUserId: String): Boolean =
        getFriends(userId).any { it.id == otherUserId }

    fun addFriend(userId: String = MockClosifyData.CURRENT_USER_ID, friendId: String) {
        MockClosifyData.addFriend(userId, friendId)
    }

    fun removeFriend(userId: String = MockClosifyData.CURRENT_USER_ID, friendId: String) {
        MockClosifyData.removeFriend(userId, friendId)
    }

    fun currentDateLabel(): String = MockClosifyData.CURRENT_DATE_LABEL

    fun publicProfileBaseGarmentsCount(): Int = MockClosifyData.PUBLIC_PROFILE_BASE_GARMENTS_COUNT

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
        return sortPostsNewestFirst(
            MockClosifyData.outfitPosts
            .filter { it.author.id in friendIds }
        )
    }

    fun getPostsByUser(userId: String): List<OutfitPost> =
        sortPostsNewestFirst(MockClosifyData.outfitPosts.filter { it.author.id == userId })

    fun getPostsByAuthors(userIds: Set<String>): List<OutfitPost> =
        sortPostsNewestFirst(MockClosifyData.outfitPosts.filter { it.author.id in userIds })

    fun sortPostsNewestFirst(posts: List<OutfitPost>): List<OutfitPost> =
        posts.sortedByDescending { it.createdAt.toMockDateOrder() }

    private fun String.toMockDateOrder(): Int {
        val parts = split(" de ")
        if (parts.size != 3) return 0

        val day = parts[0].toIntOrNull() ?: return 0
        val month = when (parts[1].lowercase()) {
            "enero" -> 1
            "febrero" -> 2
            "marzo" -> 3
            "abril" -> 4
            "mayo" -> 5
            "junio" -> 6
            "julio" -> 7
            "agosto" -> 8
            "septiembre" -> 9
            "octubre" -> 10
            "noviembre" -> 11
            "diciembre" -> 12
            else -> return 0
        }
        val year = parts[2].toIntOrNull() ?: return 0

        return year * 10_000 + month * 100 + day
    }
}
