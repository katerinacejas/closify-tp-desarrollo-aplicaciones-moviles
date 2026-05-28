package com.closify.myapplication.ui.screens.friends

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.viewmodel.FriendsViewModel

@Composable
fun FriendsScreen(
    onNotificationsClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    FriendsContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNotificationsClick = onNotificationsClick,
        onLikeClick = viewModel::onLikeClick,
        onCommentDraftChange = viewModel::onCommentDraftChange,
        onSendComment = viewModel::onSendComment,
        onUserClick = onOpenUserProfile,
        onToggleFriend = viewModel::onToggleFriend
    )
}
