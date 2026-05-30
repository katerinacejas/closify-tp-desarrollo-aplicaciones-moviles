package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.domain.model.NotificationType
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PublicProfileViewModelTest {

    @Before
    fun setUp() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @After
    fun tearDown() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @Test
    fun loadProfile_friendShowsPosts() {
        val viewModel = PublicProfileViewModel()

        viewModel.loadProfile("user_2")

        val state = viewModel.uiState.value
        assertTrue(state.isFriend)
        assertTrue(state.posts.isNotEmpty())
    }

    @Test
    fun toggleNonFriend_sendsPendingRequestWithoutShowingPosts() {
        val viewModel = PublicProfileViewModel()
        val userId = "user_12"

        viewModel.loadProfile(userId)
        if (viewModel.uiState.value.isFriend) {
            viewModel.onToggleFriend(userId)
        }

        assertFalse(viewModel.uiState.value.isFriend)

        viewModel.onToggleFriend(userId)
        assertFalse(viewModel.uiState.value.isFriend)
        assertTrue(viewModel.uiState.value.hasPendingOutgoingRequest)
    }

    @Test
    fun likingPostFromPublicProfile_persistsLikeAndCreatesNotificationForAuthor() {
        val viewModel = PublicProfileViewModel()
        val postId = "post_18"

        viewModel.loadProfile(MockClosifyData.JUAN_USER_ID)
        viewModel.onLikeClick(postId)

        val updatedPost = MockClosifyData.outfitPosts.first { it.id == postId }
        assertTrue(updatedPost.likedBy.any { it.user.id == MockClosifyData.MARIA_USER_ID })
        assertTrue(viewModel.uiState.value.posts.first { it.id == postId }.likedBy.any {
            it.user.id == MockClosifyData.MARIA_USER_ID
        })

        val juanNotifications = NotificationRepository.instance.getNotifications(MockClosifyData.JUAN_USER_ID)
        assertTrue(
            juanNotifications.any {
                it.type == NotificationType.POST_LIKE &&
                        it.postId == postId &&
                        it.sender.id == MockClosifyData.MARIA_USER_ID
            }
        )
    }

    @Test
    fun sendingCommentFromPublicProfile_persistsCommentAndCreatesNotificationForAuthor() {
        val viewModel = PublicProfileViewModel()
        val postId = "post_18"
        val commentText = "Re va para el finde"

        viewModel.loadProfile(MockClosifyData.JUAN_USER_ID)
        viewModel.onCommentDraftChange(postId, commentText)
        viewModel.onSendComment(postId)

        val updatedPost = MockClosifyData.outfitPosts.first { it.id == postId }
        assertTrue(
            updatedPost.comments.any {
                it.user.id == MockClosifyData.MARIA_USER_ID && it.text == commentText
            }
        )
        assertFalse(viewModel.uiState.value.commentDrafts.containsKey(postId))

        val juanNotifications = NotificationRepository.instance.getNotifications(MockClosifyData.JUAN_USER_ID)
        assertTrue(
            juanNotifications.any {
                it.type == NotificationType.POST_COMMENT &&
                        it.postId == postId &&
                        it.sender.id == MockClosifyData.MARIA_USER_ID
            }
        )
    }
}
