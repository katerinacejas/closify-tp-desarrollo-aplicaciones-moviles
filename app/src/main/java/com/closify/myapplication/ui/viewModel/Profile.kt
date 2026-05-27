package com.closify.myapplication.ui.viewModel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.closify.myapplication.R
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
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

        _uiState.value = ProfileUiState(
            name = profile.name,
            username = profile.username,
            bio = profile.bio,
            birthDate = profile.birthDate,
            friendsCount = profile.friendsCount,
            garmentsCount = profile.garmentsCount,
            wardrobeUsagePercentage = profile.wardrobeUsagePercentage,
            favoriteOutfitsCount = profile.favoriteOutfitsCount,
            plannedOutfitsCount = profile.plannedOutfitsCount,
            bannerImageResId = profile.bannerImageResId,
            profileImageResId = profile.profileImageResId,
            friends = profileRepository.getFriends(),
            posts = profileRepository.getPosts()
        )
    }

    fun onLikeClick(postId: String) {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val nextLiked = !post.isLiked
                    val myLike = Like(
                        id = "me",
                        user = UserSummary(
                            id = "user_1",
                            name = currentState.name,
                            username = currentState.username,
                            profileImageResId = currentState.profileImageResId ?: R.drawable.avatar_default
                        ),
                        createdAt = "25 de mayo de 2026"
                    )
                    post.copy(
                        isLiked = nextLiked,
                        likedBy = if (nextLiked) {
                            listOf(myLike) + post.likedBy
                        } else {
                            post.likedBy.filterNot { it.id == myLike.id }
                        }
                    )
                } else {
                    post
                }
            }
        )
    }

    fun onCommentClick(postId: String) {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val myComment = Comment(
                        id = "me_comment_${post.commentsCount + 1}",
                        user = UserSummary(
                            id = "user_1",
                            name = currentState.name,
                            username = currentState.username,
                            profileImageResId = currentState.profileImageResId ?: R.drawable.avatar_default
                        ),
                        text = "Me encanta este outfit <3",
                        createdAt = "25 de mayo de 2026"
                    )

                    post.copy(comments = post.comments + myComment)
                } else {
                    post
                }
            }
        )
    }
}
