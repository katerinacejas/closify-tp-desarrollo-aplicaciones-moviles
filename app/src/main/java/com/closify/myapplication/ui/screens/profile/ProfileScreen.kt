package com.closify.myapplication.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.screens.profile.components.ProfileTopBar
import com.closify.myapplication.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    targetPostId: String? = null,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(onSettingsClick = onSettingsClick)
        }
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            onLikeClick = viewModel::onLikeClick,
            onUpdatePostTitle = viewModel::onUpdatePostTitle,
            onDeletePost = viewModel::onDeletePost,
            onOpenUserProfile = onOpenUserProfile,
            onToggleFriend = viewModel::onToggleFriend,
            targetPostId = targetPostId,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
