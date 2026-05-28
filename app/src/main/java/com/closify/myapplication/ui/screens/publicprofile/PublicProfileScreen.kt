package com.closify.myapplication.ui.screens.publicprofile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.components.ClosifyTopBar
import com.closify.myapplication.ui.viewmodel.PublicProfileViewModel

@Composable
fun PublicProfileScreen(
    userId: String,
    onOpenUserProfile: (String) -> Unit,
    viewModel: PublicProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ClosifyTopBar()
        }
    ) { innerPadding ->
        PublicProfileContent(
            uiState = uiState,
            onOpenUserProfile = onOpenUserProfile,
            onToggleFriend = viewModel::onToggleFriend,
            onAcceptIncomingFriendRequest = viewModel::onAcceptIncomingFriendRequest,
            onRejectIncomingFriendRequest = viewModel::onRejectIncomingFriendRequest,
            onLikeClick = viewModel::onLikeClick,
            onCommentDraftChange = viewModel::onCommentDraftChange,
            onSendComment = viewModel::onSendComment,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
