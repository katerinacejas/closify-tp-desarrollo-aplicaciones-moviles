package com.closify.myapplication.ui.screens.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    onOpenUserProfile: (String) -> Unit,
    onOpenPostInProfile: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    NotificationsContent(
        uiState = uiState,
        onOpenUserProfile = onOpenUserProfile,
        onOpenPostInProfile = onOpenPostInProfile,
        onBackClick = onBackClick,
        onAcceptFriendRequest = viewModel::onAcceptFriendRequest,
        onRejectFriendRequest = viewModel::onRejectFriendRequest
    )
}
