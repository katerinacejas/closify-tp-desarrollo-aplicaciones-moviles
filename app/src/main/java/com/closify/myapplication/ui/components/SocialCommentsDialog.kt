package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Comment

@Composable
fun SocialCommentsDialog(
    comments: List<Comment>,
    emptyMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    commentValue: String = "",
    onCommentValueChange: (String) -> Unit = {},
    onSendComment: () -> Unit = {},
    showInput: Boolean = false,
    onUserClick: (String) -> Unit = {}
) {
    SocialDialogScaffold(
        title = "Comentarios",
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (comments.isEmpty()) {
            SocialDialogEmptyContent(
                icon = Icons.Rounded.ChatBubbleOutline,
                message = emptyMessage,
                modifier = Modifier.weight(1f),
                iconSize = 66.dp
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(comments) { index, comment ->
                    SocialDialogUserRow(
                        user = comment.user,
                        supportingText = "Comento el ${comment.createdAt}",
                        rowHeight = null,
                        onUserClick = { onUserClick(comment.user.id) }
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

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

        if (showInput) {
            ClosifyTextField(
                value = commentValue,
                onValueChange = onCommentValueChange,
                placeholder = "Escribi un comentario",
                modifier = Modifier.padding(top = 10.dp),
                trailingContent = {
                    IconButton(onClick = onSendComment) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Enviar comentario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}
