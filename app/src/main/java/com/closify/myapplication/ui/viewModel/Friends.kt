package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            val user = userRepository.getCurrentUserOrDefault().toSummary()
            friendIds = socialRepository.getFriends(user.id).map { it.id }.toSet()
            val posts = outfitPostRepository.getPostsByAuthors(friendIds)
            val unreadCount = notificationRepository.getUnreadCount(user.id)
            
            _uiState.update { it.copy(
                currentUser = user,
                friendsCount = friendIds.size,
                posts = posts,
                hasUnreadNotifications = unreadCount > 0
            ) }
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentUserId = currentState.currentUser.id
            val allUsers = socialRepository.searchUserSummariesByName(query, currentUserId)
            
            val results = allUsers.map { user ->
                FriendSearchResult(
                    user = user,
                    relationshipStatus = relationshipStatusFor(user.id, currentUserId)
                )
            }

            _uiState.update { it.copy(
                searchQuery = query,
                friendSearchResults = results.filter { it.isFriend },
                otherSearchResults = results.filterNot { it.isFriend }
            ) }
        }
    }

    fun onToggleFriend(userId: String) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            if (userId in friendIds) {
                socialRepository.removeFriend(currentUserId, userId)
            } else {
                socialRepository.sendFriendRequest(currentUserId, userId)
            }
            
            // Recargamos datos
            friendIds = socialRepository.getFriends(currentUserId).map { it.id }.toSet()
            val posts = outfitPostRepository.getPostsByAuthors(friendIds)
            
            _uiState.update { state ->
                val allSearchResults = (state.friendSearchResults + state.otherSearchResults)
                    .distinctBy { it.user.id }
                
                val updatedResults = allSearchResults.map { item ->
                    item.copy(relationshipStatus = relationshipStatusFor(item.user.id, currentUserId))
                }

                state.copy(
                    friendsCount = friendIds.size,
                    posts = posts,
                    friendSearchResults = updatedResults.filter { it.isFriend },
                    otherSearchResults = updatedResults.filterNot { it.isFriend }
                )
            }
        }
    }

    fun onCommentDraftChange(postId: String, value: String) {
        _uiState.update { it.copy(
            commentDrafts = it.commentDrafts + (postId to value)
        ) }
    }

    fun onLikeClick(postId: String) {
        viewModelScope.launch {
            val updatedPost = outfitPostRepository.toggleLike(
                postId = postId,
                user = _uiState.value.currentUser
            ) ?: return@launch

            replacePost(updatedPost)
        }
    }

    fun onSendComment(postId: String) {
        viewModelScope.launch {
            val text = _uiState.value.commentDrafts[postId].orEmpty()
            val user = _uiState.value.currentUser
            val updatedPost = outfitPostRepository.addComment(
                postId = postId,
                user = user,
                text = text
            ) ?: return@launch

            _uiState.update { it.copy(
                posts = it.posts.map { post -> if (post.id == postId) updatedPost else post },
                commentDrafts = it.commentDrafts - postId
            ) }
        }
    }

    private fun replacePost(updatedPost: OutfitPost) {
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }
            )
        }
    }

    private suspend fun relationshipStatusFor(userId: String, currentUserId: String): FriendRelationshipStatus {
        return when {
            userId in friendIds -> FriendRelationshipStatus.FRIEND
            socialRepository.getPendingOutgoingFriendRequest(currentUserId, userId) != null -> FriendRelationshipStatus.OUTGOING_PENDING
            else -> FriendRelationshipStatus.NONE
        }
    }
}
