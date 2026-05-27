package com.closify.myapplication.ui.screens.profile.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.UserSummary

@Composable
fun FriendsDialog(
    friends: List<UserSummary>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileDialogScaffold(
        title = "Mis amigos",
        onDismiss = onDismiss,
        modifier = modifier,
        contentTopSpacing = 8.dp
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(friends) { friend ->
                FriendRow(friend = friend)
            }
        }
    }
}

@Composable
private fun FriendRow(
    friend: UserSummary,
    modifier: Modifier = Modifier
) {
    ProfileDialogUserRow(
        user = friend,
        supportingText = friend.username,
        modifier = modifier,
        rowHeight = 52.dp,
        trailingContent = {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "Eliminar",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    )
}
