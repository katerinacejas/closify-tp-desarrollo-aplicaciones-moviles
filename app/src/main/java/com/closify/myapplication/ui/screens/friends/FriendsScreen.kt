package com.closify.myapplication.ui.screens.friends

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.viewmodel.FriendsViewModel

@Composable
fun FriendsScreen(
    onNotificationsClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    viewModel: FriendsViewModel = viewModel(
        factory = FriendsViewModel.factory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
