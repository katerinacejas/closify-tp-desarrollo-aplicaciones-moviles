package com.closify.myapplication.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        ProfileContent(
            uiState = uiState,
            onSettingsClick = onSettingsClick,
            onLikeClick = viewModel::onLikeClick,
            onUpdatePostTitle = viewModel::onUpdatePostTitle,
            onDeletePost = viewModel::onDeletePost,
            onOpenUserProfile = onOpenUserProfile,
            onToggleFriend = viewModel::onToggleFriend,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
