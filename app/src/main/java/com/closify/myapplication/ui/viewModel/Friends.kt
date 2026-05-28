package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FriendSearchResult(
    val user: UserSummary,
    val isFriend: Boolean
)

data class FriendsUiState(
    val currentUser: UserSummary,
    val searchQuery: String = "",
    val friendsCount: Int = 0,
    val friendSearchResults: List<FriendSearchResult> = emptyList(),
    val otherSearchResults: List<FriendSearchResult> = emptyList(),
    val commentDrafts: Map<String, String> = emptyMap(),
    val posts: List<OutfitPost> = emptyList()
)

class FriendsViewModel(
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FriendsUiState(currentUser = userRepository.getCurrentUserOrDefault().toSummary())
    )
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()
    private var friendIds: Set<String> = emptySet()
    private var allPosts: List<OutfitPost> = emptyList()

    init {
        loadFeed()
    }

    fun refresh() {
        loadFeed()
    }

    private fun loadFeed() {
        val user = userRepository.getCurrentUserOrDefault().toSummary()
        friendIds = socialRepository.getFriends(user.id).map { it.id }.toSet()
        val allUserIds = socialRepository.getAllUserSummaries(user.id).map { it.id }.toSet()
        allPosts = socialRepository.getPostsByAuthors(allUserIds)
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            friendsCount = friendIds.size,
            posts = buildFeedPosts()
        )
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        val cleanQuery = query.trim().removePrefix("@")
        val allResults = if (cleanQuery.isBlank()) {
            emptyList()
        } else {
            socialRepository.getAllUserSummaries(currentState.currentUser.id)
                .filter { it.name.startsWith(cleanQuery, ignoreCase = true) }
                .map { user ->
                    FriendSearchResult(
                        user = user,
                        isFriend = user.id in friendIds
                    )
                }
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
            socialRepository.addFriend(currentUserId, userId)
        }
        friendIds = socialRepository.getFriends(currentUserId).map { it.id }.toSet()

        val currentState = _uiState.value

        val updatedSearchResults = (currentState.friendSearchResults + currentState.otherSearchResults)
            .distinctBy { it.user.id }
            .map { it.copy(isFriend = it.user.id in friendIds) }

        _uiState.value = currentState.copy(
            friendsCount = friendIds.size,
            posts = buildFeedPosts(),
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
        val currentState = _uiState.value
        val currentUser = currentState.currentUser

        fun OutfitPost.toggleLike(): OutfitPost {
            val alreadyLiked = likedBy.any { it.user.id == currentUser.id }
            val myLike = Like(
                id = "like_${currentUser.id}_$id",
                user = currentUser,
                createdAt = socialRepository.currentDateLabel()
            )

            return copy(
                likedBy = if (alreadyLiked) {
                    likedBy.filterNot { it.user.id == currentUser.id }
                } else {
                    listOf(myLike) + likedBy
                }
            )
        }

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) post.toggleLike() else post
            }
        )
        allPosts = allPosts.map { post ->
            if (post.id == postId) post.toggleLike() else post
        }
    }

    fun onSendComment(postId: String) {
        val text = _uiState.value.commentDrafts[postId].orEmpty()
        val cleanText = text.trim()
        if (cleanText.isBlank()) return

        val currentState = _uiState.value
        val currentUser = currentState.currentUser

        fun OutfitPost.addComment(): OutfitPost = copy(
            comments = comments + Comment(
                id = "comment_${currentUser.id}_${id}_${comments.size + 1}",
                user = currentUser,
                text = cleanText,
                createdAt = socialRepository.currentDateLabel()
            )
        )

        _uiState.value = currentState.copy(
            posts = currentState.posts.map { post ->
                if (post.id == postId) {
                    post.addComment()
                } else {
                    post
                }
            },
            commentDrafts = currentState.commentDrafts - postId
        )
        allPosts = allPosts.map { post ->
            if (post.id == postId) post.addComment() else post
        }
    }

    private fun buildFeedPosts(): List<OutfitPost> =
        socialRepository.sortPostsNewestFirst(allPosts.filter { it.author.id in friendIds })
}
