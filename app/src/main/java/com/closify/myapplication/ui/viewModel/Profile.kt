package com.closify.myapplication.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

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
    val bannerImageUrl: String? = null,
    val profileImageUrl: String? = null,
    val friends: List<UserSummary> = emptyList(),
    val posts: List<OutfitPost> = emptyList()
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.instance,
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
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
        viewModelScope.launch {
            val userId = userRepository.getCurrentUser()?.id ?: return@launch
            loadProfile(userId)
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            val profile = user.profile
            val friends = socialRepository.getFriends(userId)
            val posts = outfitPostRepository.getPostsByUser(userId)
            val stats = profileRepository.getProfileStats(userId)

            _uiState.update { it.copy(
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
                bannerImageUrl = profile.bannerImageUrl,
                profileImageUrl = profile.avatarImageUrl,
                friends = friends,
                posts = posts
            ) }
        }
    }

    fun onLikeClick(postId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentUserSummary = UserSummary(
                id = currentState.userId,
                fullName = currentState.name,
                username = currentState.username,
                profileImageResId = currentState.profileImageResId ?: return@launch,
                profileImageUrl = currentState.profileImageUrl
            )
            val updatedPost = outfitPostRepository.toggleLike(postId = postId, user = currentUserSummary) ?: return@launch
            analyticsTracker.track(AnalyticsEvents.postLiked("profile"))

            _uiState.update { state ->
                state.copy(
                    posts = state.posts.map { post ->
                        if (post.id == postId) updatedPost else post
                    }
                )
            }
        }
    }

    fun onUpdatePostTitle(postId: String, title: String) {
        viewModelScope.launch {
            runCatching {
                outfitPostRepository.updatePostTitle(postId, title) ?: return@launch
            }.onSuccess {
                analyticsTracker.track(AnalyticsEvents.postTitleUpdated())
                refreshProfile()
            }.onFailure { error ->
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf("feature" to "profile", "operation" to "update_post_title")
                )
            }
        }
    }

    fun onDeletePost(postId: String) {
        viewModelScope.launch {
            runCatching {
                outfitPostRepository.deletePost(postId)
            }.onSuccess {
                analyticsTracker.track(AnalyticsEvents.postDeleted())
                refreshProfile()
            }.onFailure { error ->
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf("feature" to "profile", "operation" to "delete_post")
                )
            }
        }
    }

    fun onToggleFriend(friendId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isFriend = currentState.friends.any { it.id == friendId }

            val operationResult = if (isFriend) {
                runCatching { socialRepository.removeFriend(currentState.userId, friendId) }
            } else {
                // En el perfil solemos enviar solicitud en lugar de agregar directo
                socialRepository.sendFriendRequest(currentState.userId, friendId)
            }

            operationResult
                .onSuccess {
                    analyticsTracker.track(
                        if (isFriend) AnalyticsEvents.friendRemoved("profile")
                        else AnalyticsEvents.friendRequestSent("profile")
                    )
                }
                .onFailure { error ->
                    crashReporter.recordException(
                        throwable = error,
                        keys = mapOf(
                            "feature" to "social",
                            "operation" to if (isFriend) "remove_friend" else "send_friend_request",
                            "surface" to "profile"
                        )
                    )
                }

            val friends = socialRepository.getFriends(currentState.userId)
            _uiState.update { it.copy(
                friends = friends,
                friendsCount = friends.size
            ) }
        }
    }
}
