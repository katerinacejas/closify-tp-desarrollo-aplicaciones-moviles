package com.closify.myapplication.ui.screens.publicprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.ui.components.OutfitPostCard
import com.closify.myapplication.ui.components.ProfileFriendsDialog
import com.closify.myapplication.ui.components.SocialCommentsDialog
import com.closify.myapplication.ui.components.SocialLikesDialog
import com.closify.myapplication.ui.screens.profile.components.ProfileHeader
import com.closify.myapplication.ui.screens.profile.components.ProfileStats
import com.closify.myapplication.ui.viewmodel.PublicProfileUiState

@Composable
fun PublicProfileContent(
    uiState: PublicProfileUiState,
    onOpenUserProfile: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    onAcceptIncomingFriendRequest: (String) -> Unit,
    onRejectIncomingFriendRequest: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentDraftChange: (String, String) -> Unit,
    onSendComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = uiState.profile ?: return
    var showFriendsDialog by remember { mutableStateOf(false) }
    var selectedLikesPostId by remember { mutableStateOf<String?>(null) }
    var selectedCommentsPostId by remember { mutableStateOf<String?>(null) }
    val selectedLikesPost = uiState.posts.firstOrNull { it.id == selectedLikesPostId }
    val selectedCommentsPost = uiState.posts.firstOrNull { it.id == selectedCommentsPostId }
    val pendingIncomingRequest = uiState.pendingIncomingRequest
    val friendActionText = when {
        uiState.isFriend -> stringResource(R.string.friends_action_remove)
        uiState.hasPendingOutgoingRequest -> stringResource(R.string.friends_action_pending)
        else -> stringResource(R.string.friends_action_add)
    }

    if (showFriendsDialog) {
        ProfileFriendsDialog(
            friends = uiState.friends,
            onFriendClick = { userId ->
                showFriendsDialog = false
                onOpenUserProfile(userId)
            },
            onToggleFriend = null,
            onDismiss = { showFriendsDialog = false }
        )
    }

    if (selectedLikesPost != null) {
        SocialLikesDialog(
            likes = selectedLikesPost.likedBy,
            emptyMessage = stringResource(R.string.friends_likes_empty),
            onUserClick = { userId ->
                selectedLikesPostId = null
                onOpenUserProfile(userId)
            },
            onDismiss = { selectedLikesPostId = null }
        )
    }

    if (selectedCommentsPost != null) {
        SocialCommentsDialog(
            comments = selectedCommentsPost.comments,
            emptyMessage = stringResource(R.string.friends_comments_empty),
            commentValue = uiState.commentDrafts[selectedCommentsPost.id].orEmpty(),
            onCommentValueChange = { value ->
                onCommentDraftChange(selectedCommentsPost.id, value)
            },
            onSendComment = { onSendComment(selectedCommentsPost.id) },
            showInput = true,
            onUserClick = { userId ->
                selectedCommentsPostId = null
                onOpenUserProfile(userId)
            },
            onDismiss = { selectedCommentsPostId = null }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box {
                ProfileHeader(
                    name = profile.name,
                    username = profile.username,
                    bio = profile.bio,
                    birthDate = profile.birthDate,
                    friendsCount = uiState.friendsCount,
                    bannerImageResId = profile.bannerImageResId,
                    profileImageResId = profile.profileImageResId,
                    onFriendsClick = { showFriendsDialog = true }
                )

                if (pendingIncomingRequest == null) {
                    Button(
                        onClick = { onToggleFriend(profile.id) },
                        enabled = !uiState.hasPendingOutgoingRequest,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 16.dp)
                            .offset(y = 112.dp)
                            .height(34.dp)
                    ) {
                        Text(
                            text = friendActionText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        if (uiState.isFriend) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ProfileStats(
                    garmentsCount = uiState.garmentsCount,
                    wardrobeUsage = uiState.wardrobeUsagePercentage,
                    favoriteOutfits = uiState.favoriteOutfitsCount,
                    plannedOutfits = uiState.plannedOutfitsCount
                )
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(uiState.posts) { post ->
                PublicProfilePostCard(
                    post = post,
                    currentUserId = uiState.currentUser.id,
                    onLikeClick = onLikeClick,
                    onLikesTextClick = { selectedLikesPostId = it },
                    onCommentsClick = { selectedCommentsPostId = it }
                )
            }
        } else if (pendingIncomingRequest != null) {
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                IncomingFriendRequestCard(
                    username = profile.username,
                    request = pendingIncomingRequest,
                    onAcceptIncomingFriendRequest = onAcceptIncomingFriendRequest,
                    onRejectIncomingFriendRequest = onRejectIncomingFriendRequest,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                PrivateFriendProfileCard(
                    username = profile.username,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun IncomingFriendRequestCard(
    username: String,
    request: FriendRequest,
    onAcceptIncomingFriendRequest: (String) -> Unit,
    onRejectIncomingFriendRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(358.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.public_profile_friend_request, username),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.public_profile_private_info, username),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.public_profile_accept_prompt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onRejectIncomingFriendRequest(request.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.notifications_action_reject),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Button(
                        onClick = { onAcceptIncomingFriendRequest(request.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.notifications_action_accept),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicProfilePostCard(
    post: OutfitPost,
    currentUserId: String,
    onLikeClick: (String) -> Unit,
    onLikesTextClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit
) {
    OutfitPostCard(
        post = post,
        isLikedByCurrentUser = post.likedBy.any { it.user.id == currentUserId },
        onLikeClick = { onLikeClick(post.id) },
        onLikesTextClick = { onLikesTextClick(post.id) },
        onCommentsClick = { onCommentsClick(post.id) },
        onCommentsTextClick = { onCommentsClick(post.id) },
        showAuthor = false,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun PrivateFriendProfileCard(
    username: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(358.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.public_profile_not_friend, username),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.public_profile_private_info, username),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.public_profile_add_prompt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
