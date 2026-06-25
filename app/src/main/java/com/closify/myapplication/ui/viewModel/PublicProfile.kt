package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.OutfitPostRepository
import kotlinx.coroutines.launch
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserProfile
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PublicProfileUiState(
    val currentUser: UserSummary,
    val profile: UserProfile? = null,
    val isFriend: Boolean = false,
    val hasPendingOutgoingRequest: Boolean = false,
    val pendingIncomingRequest: FriendRequest? = null,
    val friends: List<UserSummary> = emptyList(),
    val garmentsCount: Int = 0,
    val wardrobeUsagePercentage: Int = 0,
    val favoriteOutfitsCount: Int = 0,
    val plannedOutfitsCount: Int = 0,
    val commentDrafts: Map<String, String> = emptyMap(),
    val posts: List<OutfitPost> = emptyList()
) {
    val friendsCount: Int
        get() = friends.size
}

class PublicProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.instance,
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PublicProfileUiState(currentUser = userRepository.getCurrentUserOrDefault().toSummary())
    )
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    private var profileUserId: String? = null

    fun loadProfile(userId: String) {
        profileUserId = userId
        refresh()
    }

    fun refresh() {
        val userId = profileUserId ?: return
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUserOrDefault().toSummary()
            val profile = profileRepository.getProfile(userId) ?: return@launch
            val posts = outfitPostRepository.getPostsByUser(userId)
            val friends = socialRepository.getFriends(userId)
            val stats = profileRepository.getPublicProfileStats(userId)
            val isFriend = socialRepository.isFriend(currentUser.id, userId)
            val pendingOutgoing = socialRepository.getPendingOutgoingFriendRequest(currentUser.id, userId)
            val pendingIncoming = socialRepository.getPendingIncomingFriendRequest(currentUser.id, userId)

            _uiState.update { it.copy(
                currentUser = currentUser,
                profile = profile,
                isFriend = isFriend,
                hasPendingOutgoingRequest = pendingOutgoing != null,
                pendingIncomingRequest = pendingIncoming,
                friends = friends,
                garmentsCount = stats.garmentsCount,
                wardrobeUsagePercentage = stats.wardrobeUsagePercentage,
                favoriteOutfitsCount = stats.favoriteOutfitsCount,
                plannedOutfitsCount = stats.plannedOutfitsCount,
                posts = posts
            ) }
        }
    }

    fun onToggleFriend(userId: String) {
        viewModelScope.launch {
            val currentUserId = _uiState.value.currentUser.id
            if (socialRepository.isFriend(currentUserId, userId)) {
                socialRepository.removeFriend(currentUserId, userId)
            } else if (socialRepository.getPendingOutgoingFriendRequest(currentUserId, userId) == null) {
                socialRepository.sendFriendRequest(currentUserId, userId)
            }
            refresh()
        }
    }

    fun onAcceptIncomingFriendRequest(requestId: String) {
        viewModelScope.launch {
            socialRepository.respondToFriendRequest(requestId, accepted = true)
            refresh()
        }
    }

    fun onRejectIncomingFriendRequest(requestId: String) {
        viewModelScope.launch {
            socialRepository.respondToFriendRequest(requestId, accepted = false)
            refresh()
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
            val text = _uiState.value.commentDrafts[postId].orEmpty().trim()
            if (text.isBlank()) return@launch

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
}
