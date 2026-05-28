package com.closify.myapplication.ui.screens.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.ui.components.SocialLikesDialog

@Composable
fun LikesDialog(
    likes: List<Like>,
    onUserClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SocialLikesDialog(
        likes = likes,
        emptyMessage = "Aun no tenes ningun me gusta en este outfit",
        onDismiss = onDismiss,
        modifier = modifier,
        onUserClick = onUserClick
    )
}
