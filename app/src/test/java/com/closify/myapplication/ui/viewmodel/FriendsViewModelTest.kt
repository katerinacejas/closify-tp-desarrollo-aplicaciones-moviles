package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FriendsViewModelTest {

    @Before
    fun setUp() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @After
    fun tearDown() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @Test
    fun initialFeed_hasCurrentUserFriendsPosts() {
        val viewModel = FriendsViewModel()
        val state = viewModel.uiState.value

        assertTrue(state.friendsCount > 0)
        assertTrue(state.posts.isNotEmpty())
        assertTrue(state.posts.all { it.author.id != state.currentUser.id })
    }

    @Test
    fun togglingNonFriend_sendsPendingRequestWithoutShowingPosts() {
        val viewModel = FriendsViewModel()
        val userId = "user_12"

        if (viewModel.uiState.value.posts.any { it.author.id == userId }) {
            viewModel.onToggleFriend(userId)
        }

        assertFalse(viewModel.uiState.value.posts.any { it.author.id == userId })

        viewModel.onToggleFriend(userId)

        val state = viewModel.uiState.value
        assertFalse(state.posts.any { it.author.id == userId })

        viewModel.onSearchQueryChange("Ayito")
        val searchResult = viewModel.uiState.value.otherSearchResults.first { it.user.id == userId }
        assertTrue(searchResult.relationshipStatus == FriendRelationshipStatus.OUTGOING_PENDING)

        viewModel.onToggleFriend(userId)
    }

    @Test
    fun togglingFriend_removesFriendAndHidesTheirPosts() {
        val viewModel = FriendsViewModel()
        val userId = "user_2"

        if (viewModel.uiState.value.posts.none { it.author.id == userId }) {
            viewModel.onToggleFriend(userId)
        }

        assertTrue(viewModel.uiState.value.posts.any { it.author.id == userId })

        viewModel.onToggleFriend(userId)

        val state = viewModel.uiState.value
        assertFalse(state.posts.any { it.author.id == userId })

        viewModel.onToggleFriend(userId)
    }
}
