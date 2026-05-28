package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Like

@Composable
fun SocialLikesDialog(
    likes: List<Like>,
    emptyMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit = {}
) {
    SocialDialogScaffold(
        title = "Me gustas",
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (likes.isEmpty()) {
            SocialDialogEmptyContent(
                icon = Icons.Rounded.FavoriteBorder,
                message = emptyMessage,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(likes) { like ->
                    SocialDialogUserRow(
                        user = like.user,
                        supportingText = "Dio me gusta el ${like.createdAt}",
                        onUserClick = { onUserClick(like.user.id) }
                    )
                }
            }
        }
    }
}
