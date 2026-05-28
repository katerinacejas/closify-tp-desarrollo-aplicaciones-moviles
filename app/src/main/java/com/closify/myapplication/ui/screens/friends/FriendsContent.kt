package com.closify.myapplication.ui.screens.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.ui.components.OutfitPostCard
import com.closify.myapplication.ui.components.SocialCommentsDialog
import com.closify.myapplication.ui.components.SocialLikesDialog
import com.closify.myapplication.ui.screens.friends.components.FriendsTopBar
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.theme.PrimaryDark
import com.closify.myapplication.ui.theme.RosaSecondary
import com.closify.myapplication.ui.viewmodel.FriendSearchResult
import com.closify.myapplication.ui.viewmodel.FriendsUiState

@Composable
fun FriendsContent(
    uiState: FriendsUiState,
    onSearchQueryChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentDraftChange: (String, String) -> Unit,
    onSendComment: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLikesPostId by remember { mutableStateOf<String?>(null) }
    var selectedCommentsPostId by remember { mutableStateOf<String?>(null) }
    val selectedLikesPost = uiState.posts.firstOrNull { it.id == selectedLikesPostId }
    val selectedCommentsPost = uiState.posts.firstOrNull { it.id == selectedCommentsPostId }
    val isSearching = uiState.searchQuery.isNotBlank()

    if (selectedLikesPost != null) {
        SocialLikesDialog(
            likes = selectedLikesPost.likedBy,
            emptyMessage = "Este outfit aun no tiene ningun me gusta\nDale me gusta y muestrale lo cool que quedo!",
            onUserClick = { userId ->
                selectedLikesPostId = null
                onUserClick(userId)
            },
            onDismiss = { selectedLikesPostId = null }
        )
    }

    if (selectedCommentsPost != null) {
        SocialCommentsDialog(
            comments = selectedCommentsPost.comments,
            emptyMessage = "Este outfit aun no tiene ningun comentario\nComentale que tal quedo su look!",
            commentValue = uiState.commentDrafts[selectedCommentsPost.id].orEmpty(),
            onCommentValueChange = { value ->
                onCommentDraftChange(selectedCommentsPost.id, value)
            },
            onSendComment = { onSendComment(selectedCommentsPost.id) },
            showInput = true,
            onUserClick = { userId ->
                selectedCommentsPostId = null
                onUserClick(userId)
            },
            onDismiss = { selectedCommentsPostId = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FriendsTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onNotificationsClick = onNotificationsClick
            )
        }

        when {
            isSearching -> item {
                FriendsSearchResults(
                    friends = uiState.friendSearchResults,
                    otherUsers = uiState.otherSearchResults,
                    onUserClick = onUserClick,
                    onToggleFriend = onToggleFriend,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                )
            }

            uiState.friendsCount == 0 -> item {
                EmptyFriendsState(
                    title = "Aun no tenes amigos agregados para ver sus posteos de outfits favoritos o planificados",
                    subtitle = "Escribe el nombre de un amigo en la barra de busqueda y enviale una solicitud de amistad",
                    modifier = Modifier.padding(horizontal = 22.dp)
                )
            }

            uiState.posts.isEmpty() -> item {
                EmptyFriendsState(
                    title = "Aun ningun amigo compartio su outfit",
                    subtitle = "Cuando compartan sus looks, lo veras aqui",
                    modifier = Modifier.padding(horizontal = 22.dp)
                )
            }

            else -> items(uiState.posts) { post ->
                FeedPostCard(
                    post = post,
                    currentUserId = uiState.currentUser.id,
                    onLikeClick = onLikeClick,
                    onLikesTextClick = { selectedLikesPostId = it },
                    onCommentsClick = { selectedCommentsPostId = it },
                    onAuthorClick = onUserClick
                )
            }
        }
    }
}

@Composable
private fun FeedPostCard(
    post: OutfitPost,
    currentUserId: String,
    onLikeClick: (String) -> Unit,
    onLikesTextClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit
) {
    OutfitPostCard(
        post = post,
        isLikedByCurrentUser = post.likedBy.any { it.user.id == currentUserId },
        onLikeClick = { onLikeClick(post.id) },
        onLikesTextClick = { onLikesTextClick(post.id) },
        onCommentsClick = { onCommentsClick(post.id) },
        onCommentsTextClick = { onCommentsClick(post.id) },
        showAuthor = true,
        onAuthorClick = { onAuthorClick(post.author.id) },
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun FriendsSearchResults(
    friends: List<FriendSearchResult>,
    otherUsers: List<FriendSearchResult>,
    onUserClick: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (friends.isNotEmpty()) {
                Text(
                    text = "Mis amigos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center
                )

                friends.forEach { result ->
                    SearchUserRow(
                        user = result.user,
                        actionText = "Eliminar",
                        isFriend = true,
                        onUserClick = onUserClick,
                        onToggleFriend = onToggleFriend
                    )
                }
            }

            if (friends.isNotEmpty() && otherUsers.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = "Quiza estas buscando a...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            otherUsers.forEach { result ->
                SearchUserRow(
                    user = result.user,
                    actionText = "Agregar",
                    isFriend = false,
                    onUserClick = onUserClick,
                    onToggleFriend = onToggleFriend
                )
            }
        }
    }
}

@Composable
private fun SearchUserRow(
    user: UserSummary,
    actionText: String,
    isFriend: Boolean,
    onUserClick: (String) -> Unit,
    onToggleFriend: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = user.profileImageResId),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, RosaSecondary, CircleShape)
                .clickable { onUserClick(user.id) },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .clickable { onUserClick(user.id) }
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = { onToggleFriend(user.id) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFriend) RosaSecondary else PrimaryDark
            ),
            contentPadding = PaddingValues(horizontal = 14.dp),
            modifier = Modifier
                .width(86.dp)
                .height(34.dp)
        ) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun EmptyFriendsState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Amigos",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(616.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FriendsContentPreview() {
    ClosifyTheme {
        val user = UserRepository.instance.getCurrentUserOrDefault().toSummary()
        val userId = user.id

        FriendsContent(
            uiState = FriendsUiState(
                currentUser = user,
                friendsCount = SocialRepository.instance.getFriends(userId).size,
                posts = SocialRepository.instance.getFriendsFeed(userId)
            ),
            onSearchQueryChange = {},
            onNotificationsClick = {},
            onLikeClick = {},
            onCommentDraftChange = { _, _ -> },
            onSendComment = {},
            onUserClick = {},
            onToggleFriend = {}
        )
    }
}
