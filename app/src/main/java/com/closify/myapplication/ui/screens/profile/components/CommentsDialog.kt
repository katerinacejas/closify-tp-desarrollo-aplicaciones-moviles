package com.closify.myapplication.ui.screens.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.ui.components.SocialCommentsDialog

@Composable
fun CommentsDialog(
    comments: List<Comment>,
    onUserClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SocialCommentsDialog(
        comments = comments,
        emptyMessage = "Aun no tenes ningun comentario en este outfit",
        onDismiss = onDismiss,
        modifier = modifier,
        onUserClick = onUserClick
    )
}
