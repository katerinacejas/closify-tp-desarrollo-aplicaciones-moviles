package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
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
    fun toggleFriend_updatesRelationshipAndVisibilityState() {
        val viewModel = PublicProfileViewModel()
        val userId = "user_11"

        viewModel.loadProfile(userId)
        if (viewModel.uiState.value.isFriend) {
            viewModel.onToggleFriend(userId)
        }

        assertFalse(viewModel.uiState.value.isFriend)

        viewModel.onToggleFriend(userId)
        assertTrue(viewModel.uiState.value.isFriend)

        viewModel.onToggleFriend(userId)
    }
}
