package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FriendRelationshipStatus {
    FRIEND,
    OUTGOING_PENDING,
    NONE
}

data class FriendSearchResult(
    val user: UserSummary,
    val relationshipStatus: FriendRelationshipStatus
) {
    val isFriend: Boolean
        get() = relationshipStatus == FriendRelationshipStatus.FRIEND
}

data class FriendsUiState(
    val currentUser: UserSummary,
    val searchQuery: String = "",
    val friendsCount: Int = 0,
    val friendSearchResults: List<FriendSearchResult> = emptyList(),
    val otherSearchResults: List<FriendSearchResult> = emptyList(),
    val commentDrafts: Map<String, String> = emptyMap(),
    val posts: List<OutfitPost> = emptyList(),
    val hasUnreadNotifications: Boolean = false
)

class FriendsViewModel(
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val notificationRepository: NotificationRepository = NotificationRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FriendsUiState(currentUser = userRepository.getCurrentUserOrDefault().toSummary())
    )
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()
    private var friendIds: Set<String> = emptySet()

    init {
        loadFeed()
    }

    fun refresh() {
        loadFeed()
    }

    private fun loadFeed() {
        val user = userRepository.getCurrentUserOrDefault().toSummary()
        friendIds = socialRepository.getFriends(user.id).map { it.id }.toSet()
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            friendsCount = friendIds.size,
            posts = outfitPostRepository.getPostsByAuthors(friendIds),
            hasUnreadNotifications = notificationRepository.getUnreadCount(user.id) > 0
        )
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        val allResults = socialRepository.searchUserSummariesByName(
            query = query,
            userId = currentState.currentUser.id
        ).map { user ->
            FriendSearchResult(
                user = user,
                relationshipStatus = relationshipStatusFor(user.id)
            )
        }

        _uiState.value = currentState.copy(
            searchQuery = query,
            friendSearchResults = allResults.filter { it.isFriend },
            otherSearchResults = allResults.filterNot { it.isFriend }
        )
    }

    fun onToggleFriend(userId: String) {
        val currentUserId = _uiState.value.currentUser.id
        if (userId in friendIds) {
            socialRepository.removeFriend(currentUserId, userId)
        } else {
            socialRepository.sendFriendRequest(currentUserId, userId)
        }
        friendIds = socialRepository.getFriends(currentUserId).map { it.id }.toSet()

        val currentState = _uiState.value

        val updatedSearchResults = (currentState.friendSearchResults + currentState.otherSearchResults)
            .distinctBy { it.user.id }
            .map { it.copy(relationshipStatus = relationshipStatusFor(it.user.id)) }

        _uiState.value = currentState.copy(
            friendsCount = friendIds.size,
            posts = outfitPostRepository.getPostsByAuthors(friendIds),
            friendSearchResults = updatedSearchResults.filter { it.isFriend },
            otherSearchResults = updatedSearchResults.filterNot { it.isFriend }
        )
    }

    fun onCommentDraftChange(postId: String, value: String) {
        _uiState.value = _uiState.value.copy(
            commentDrafts = _uiState.value.commentDrafts + (postId to value)
        )
    }

    fun onLikeClick(postId: String) {
        val updatedPost = outfitPostRepository.toggleLike(
            postId = postId,
            user = _uiState.value.currentUser
        ) ?: return

        replacePost(updatedPost)
    }

    fun onSendComment(postId: String) {
        val text = _uiState.value.commentDrafts[postId].orEmpty()
        val currentState = _uiState.value
        val updatedPost = outfitPostRepository.addComment(
            postId = postId,
            user = currentState.currentUser,
            text = text
        ) ?: return

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post -> if (post.id == postId) updatedPost else post },
            commentDrafts = currentState.commentDrafts - postId
        )
    }

    private fun replacePost(updatedPost: OutfitPost) {
        _uiState.value = _uiState.value.copy(
            posts = _uiState.value.posts.map { post ->
                if (post.id == updatedPost.id) updatedPost else post
            }
        )
    }

    private fun relationshipStatusFor(userId: String): FriendRelationshipStatus {
        val currentUserId = _uiState.value.currentUser.id
        return when {
            userId in friendIds -> FriendRelationshipStatus.FRIEND
            socialRepository.getPendingOutgoingFriendRequest(currentUserId, userId) != null -> FriendRelationshipStatus.OUTGOING_PENDING
            else -> FriendRelationshipStatus.NONE
        }
    }
}
