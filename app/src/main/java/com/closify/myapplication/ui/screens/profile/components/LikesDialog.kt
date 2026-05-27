package com.closify.myapplication.ui.screens.profile.components

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
fun LikesDialog(
    likes: List<Like>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileDialogScaffold(
        title = "Me gustas",
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (likes.isEmpty()) {
            ProfileDialogEmptyContent(
                icon = Icons.Rounded.FavoriteBorder,
                message = "Aun no tenes ningun me gusta en este outfit",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(likes) { like ->
                    LikeRow(like = like)
                }
            }
        }
    }
}

@Composable
private fun LikeRow(
    like: Like,
    modifier: Modifier = Modifier
) {
    ProfileDialogUserRow(
        user = like.user,
        supportingText = "Dio me gusta el ${like.createdAt}",
        modifier = modifier
    )
}
