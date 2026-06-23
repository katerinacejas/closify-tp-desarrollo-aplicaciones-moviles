package com.closify.myapplication.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ProfileUiState(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val birthDate: String = "",
    val friendsCount: Int = 0,
    val garmentsCount: Int = 0,
    val wardrobeUsagePercentage: Int = 0,
    val favoriteOutfitsCount: Int = 0,
    val plannedOutfitsCount: Int = 0,
    @param:DrawableRes val bannerImageResId: Int? = null,
    @param:DrawableRes val profileImageResId: Int? = null,
    val friends: List<UserSummary> = emptyList(),
    val posts: List<OutfitPost> = emptyList()
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.instance,
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Observa el usuario logueado — se actualiza cuando restoreSession() termina
        userRepository.currentUser
            .filterNotNull()
            .onEach { user -> loadProfile(user.id) }
            .launchIn(viewModelScope)
    }

    fun refreshProfile() {
        val userId = userRepository.getCurrentUser()?.id ?: return
        loadProfile(userId)
    }

    private fun loadProfile(userId: String) {
        val user = userRepository.getCurrentUser() ?: return
        val profile = user.profile
        val friends = socialRepository.getFriends(userId)
        val posts = outfitPostRepository.getPostsByUser(userId)
        val stats = profileRepository.getProfileStats(userId)

        _uiState.value = ProfileUiState(
            userId = profile.id,
            name = profile.name,
            username = profile.username,
            bio = profile.bio,
            birthDate = profile.birthDate,
            friendsCount = friends.size,
            garmentsCount = stats.garmentsCount,
            wardrobeUsagePercentage = stats.wardrobeUsagePercentage,
            favoriteOutfitsCount = stats.favoriteOutfitsCount,
            plannedOutfitsCount = stats.plannedOutfitsCount,
            bannerImageResId = profile.bannerImageResId,
            profileImageResId = profile.profileImageResId,
            friends = friends,
            posts = posts
        )
    }

    fun onLikeClick(postId: String) {
        val currentState = _uiState.value
        val currentUser = UserSummary(
            id = currentState.userId,
            fullName = currentState.name,
            username = currentState.username,
            profileImageResId = currentState.profileImageResId ?: return
        )
        val updatedPost = outfitPostRepository.toggleLike(postId = postId, user = currentUser) ?: return

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) updatedPost else post
            }
        )
    }

    fun onUpdatePostTitle(postId: String, title: String) {
        outfitPostRepository.updatePostTitle(postId, title) ?: return
        refreshProfile()
    }

    fun onDeletePost(postId: String) {
        outfitPostRepository.deletePost(postId)
        refreshProfile()
    }

    fun onToggleFriend(friendId: String) {
        val currentState = _uiState.value
        val isFriend = currentState.friends.any { it.id == friendId }

        if (isFriend) {
            socialRepository.removeFriend(currentState.userId, friendId)
        } else {
            socialRepository.addFriend(currentState.userId, friendId)
        }

        val friends = socialRepository.getFriends(currentState.userId)
        _uiState.value = currentState.copy(
            friends = friends,
            friendsCount = friends.size
        )
    }
}
