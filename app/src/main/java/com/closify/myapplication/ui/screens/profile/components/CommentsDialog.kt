package com.closify.myapplication.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Comment

@Composable
fun CommentsDialog(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileDialogScaffold(
        title = "Comentarios",
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (comments.isEmpty()) {
            ProfileDialogEmptyContent(
                icon = Icons.Rounded.ChatBubbleOutline,
                message = "Aun no tenes ningun comentario en este outfit",
                modifier = Modifier.weight(1f),
                iconSize = 66.dp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(comments) { index, comment ->
                    CommentRow(comment = comment)

                    if (index < comments.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    ProfileDialogUserRow(
        user = comment.user,
        supportingText = "Comento el ${comment.createdAt}",
        modifier = modifier,
        rowHeight = null
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = comment.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
