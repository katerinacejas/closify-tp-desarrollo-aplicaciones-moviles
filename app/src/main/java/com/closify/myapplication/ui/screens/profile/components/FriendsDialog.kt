package com.closify.myapplication.ui.screens.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.ui.components.ProfileFriendsDialog

@Composable
fun FriendsDialog(
    friends: List<UserSummary>,
    onFriendClick: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileFriendsDialog(
        friends = friends,
        onFriendClick = onFriendClick,
        onToggleFriend = onToggleFriend,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}
