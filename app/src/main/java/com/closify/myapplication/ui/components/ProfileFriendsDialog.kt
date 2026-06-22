package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.UserSummary

@Composable
fun ProfileFriendsDialog(
    friends: List<UserSummary>,
    onFriendClick: (String) -> Unit,
    onToggleFriend: ((String) -> Unit)?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SocialDialogScaffold(
        title = stringResource(R.string.profile_friends_my_friends),
        onDismiss = onDismiss,
        modifier = modifier,
        contentTopSpacing = 8.dp
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(friends) { friend ->
                SocialDialogUserRow(
                    user = friend,
                    supportingText = friend.username,
                    rowHeight = 52.dp,
                    onUserClick = { onFriendClick(friend.id) },
                    trailingContent = {
                        if (onToggleFriend != null) {
                            Button(
                                onClick = { onToggleFriend(friend.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = ButtonDefaults.ContentPadding,
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.common_delete),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
