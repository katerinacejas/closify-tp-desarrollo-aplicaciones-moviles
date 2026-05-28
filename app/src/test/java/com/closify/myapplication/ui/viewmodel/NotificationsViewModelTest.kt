package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationsViewModelTest {

    @Before
    fun setUp() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @After
    fun tearDown() {
        MockClosifyData.resetCurrentUserFriends()
    }

    @Test
    fun notifications_loadNewestFirst() {
        val state = NotificationsViewModel().uiState.value

        assertTrue(state.notifications.isNotEmpty())
        assertEquals("hace 30 minutos", state.notifications.first().notification.createdAt)
    }

    @Test
    fun acceptingFriendRequest_addsFriendAndDisablesPendingState() {
        val viewModel = NotificationsViewModel()

        viewModel.onAcceptFriendRequest("request_1")

        val requestItem = viewModel.uiState.value.notifications
            .first { it.friendRequest?.id == "request_1" }

        assertEquals(com.closify.myapplication.domain.model.FriendRequestStatus.ACCEPTED, requestItem.friendRequest?.status)
        assertTrue(MockClosifyData.isFriend(MockClosifyData.MARIA_USER_ID, "user_3"))
    }
}
