package com.closify.myapplication.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.closify.myapplication.R
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    @param:DrawableRes val bannerImageResId: Int? = R.drawable.banner_default,
    @param:DrawableRes val profileImageResId: Int? = R.drawable.avatar_default,
    val friends: List<UserSummary> = emptyList(),
    val posts: List<OutfitPost> = emptyList()
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val profile = profileRepository.getProfile()
        val friends = profileRepository.getFriends()
        val posts = profileRepository.getPosts()
        val garments = profileRepository.getWardrobeGarments()

        _uiState.value = ProfileUiState(
            userId = profile.id,
            name = profile.name,
            username = profile.username,
            bio = profile.bio,
            birthDate = profile.birthDate,
            friendsCount = friends.size,
            garmentsCount = garments.size,
            wardrobeUsagePercentage = profileRepository.getWardrobeUsagePercentage(),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED },
            bannerImageResId = profile.bannerImageResId,
            profileImageResId = profile.profileImageResId,
            friends = friends,
            posts = posts
        )
    }

    fun onLikeClick(postId: String) {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val myLike = Like(
                        id = "me",
                        user = UserSummary(
                            id = "user_1",
                            fullName = currentState.name,
                            username = currentState.username,
                            profileImageResId = currentState.profileImageResId ?: R.drawable.avatar_default
                        ),
                        createdAt = "25 de mayo de 2026"
                    )
                    val nextLiked = post.likedBy.none { it.user.id == myLike.user.id }
                    post.copy(
                        likedBy = if (nextLiked) {
                            listOf(myLike) + post.likedBy
                        } else {
                            post.likedBy.filterNot { it.user.id == myLike.user.id }
                        }
                    )
                } else {
                    post
                }
            }
        )
    }

    fun onUpdatePostTitle(postId: String, title: String) {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) {
                    post.copy(title = title.take(100).ifBlank { null })
                } else {
                    post
                }
            }
        )
    }

    fun onDeletePost(postId: String) {
        val currentState = _uiState.value
        val deletedPost = currentState.posts.firstOrNull { it.id == postId } ?: return

        _uiState.value = currentState.copy(
            posts = currentState.posts.filterNot { it.id == postId },
            favoriteOutfitsCount = if (deletedPost.type == OutfitPostType.FAVORITE) {
                (currentState.favoriteOutfitsCount - 1).coerceAtLeast(0)
            } else {
                currentState.favoriteOutfitsCount
            },
            plannedOutfitsCount = if (deletedPost.type == OutfitPostType.PLANNED) {
                (currentState.plannedOutfitsCount - 1).coerceAtLeast(0)
            } else {
                currentState.plannedOutfitsCount
            }
        )
    }
}
