package com.closify.myapplication.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.ui.screens.profile.components.*
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.ProfileUiState

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onSettingsClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onUpdatePostTitle: (String, String) -> Unit,
    onDeletePost: (String) -> Unit,
    onOpenUserProfile: (String) -> Unit,
    onToggleFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFriendsDialog by remember { mutableStateOf(false) }
    var selectedLikesOutfitId by remember { mutableStateOf<String?>(null) }
    var selectedCommentsOutfitId by remember { mutableStateOf<String?>(null) }
    var selectedEditOutfitId by remember { mutableStateOf<String?>(null) }
    var selectedDeleteOutfitId by remember { mutableStateOf<String?>(null) }
    val selectedLikesOutfit = uiState.posts.firstOrNull { it.id == selectedLikesOutfitId }
    val selectedCommentsOutfit = uiState.posts.firstOrNull { it.id == selectedCommentsOutfitId }
    val selectedEditOutfit = uiState.posts.firstOrNull { it.id == selectedEditOutfitId }
    val selectedDeleteOutfit = uiState.posts.firstOrNull { it.id == selectedDeleteOutfitId }

    if (showFriendsDialog) {
        FriendsDialog(
            friends = uiState.friends,
            onFriendClick = { userId ->
                showFriendsDialog = false
                onOpenUserProfile(userId)
            },
            onToggleFriend = onToggleFriend,
            onDismiss = { showFriendsDialog = false }
        )
    }

    if (selectedLikesOutfit != null) {
        LikesDialog(
            likes = selectedLikesOutfit.likedBy,
            onUserClick = { userId ->
                selectedLikesOutfitId = null
                onOpenUserProfile(userId)
            },
            onDismiss = { selectedLikesOutfitId = null }
        )
    }

    if (selectedCommentsOutfit != null) {
        CommentsDialog(
            comments = selectedCommentsOutfit.comments,
            onUserClick = { userId ->
                selectedCommentsOutfitId = null
                onOpenUserProfile(userId)
            },
            onDismiss = { selectedCommentsOutfitId = null }
        )
    }

    if (selectedEditOutfit != null) {
        EditOutfitDialog(
            initialTitle = selectedEditOutfit.title,
            onSaveClick = { title ->
                onUpdatePostTitle(selectedEditOutfit.id, title)
                selectedEditOutfitId = null
            },
            onDeleteClick = {
                selectedDeleteOutfitId = selectedEditOutfit.id
                selectedEditOutfitId = null
            },
            onDismiss = { selectedEditOutfitId = null }
        )
    }

    if (selectedDeleteOutfit != null) {
        DeleteOutfitDialog(
            onCancelClick = { selectedDeleteOutfitId = null },
            onConfirmClick = {
                onDeletePost(selectedDeleteOutfit.id)
                selectedDeleteOutfitId = null
            },
            onDismiss = { selectedDeleteOutfitId = null }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ProfileTopBar(onSettingsClick = onSettingsClick)
        }

        item {
            ProfileHeader(
                name = uiState.name,
                username = uiState.username,
                bio = uiState.bio,
                birthDate = uiState.birthDate,
                friendsCount = uiState.friendsCount,
                bannerImageResId = uiState.bannerImageResId,
                profileImageResId = uiState.profileImageResId,
                onFriendsClick = { showFriendsDialog = true }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            ProfileStats(
                garmentsCount = uiState.garmentsCount,
                wardrobeUsage = uiState.wardrobeUsagePercentage,
                favoriteOutfits = uiState.favoriteOutfitsCount,
                plannedOutfits = uiState.plannedOutfitsCount
            )
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(uiState.posts) { outfit ->
            ProfileOutfitCard(
                outfit = outfit,
                isLikedByCurrentUser = outfit.likedBy.any { it.user.id == uiState.userId },
                onLikeClick = { onLikeClick(outfit.id) },
                onLikesTextClick = { selectedLikesOutfitId = outfit.id },
                onCommentsClick = { selectedCommentsOutfitId = outfit.id },
                onCommentsTextClick = { selectedCommentsOutfitId = outfit.id },
                onEditClick = { selectedEditOutfitId = outfit.id },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ProfileContentPreview() {
    ClosifyTheme {
        val repository = ProfileRepository.instance
        val profile = repository.getProfile()
        val friends = repository.getFriends()
        val posts = repository.getPosts()
        val garments = repository.getWardrobeGarments()

        ProfileContent(
            uiState = ProfileUiState(
                userId = profile.id,
                name = "Katerina Cejas",
                username = "@kate_cejas_1999",
                bio = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
                birthDate = "3 de septiembre de 1999",
                friendsCount = friends.size,
                garmentsCount = garments.size,
                wardrobeUsagePercentage = repository.getWardrobeUsagePercentage(),
                favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
                plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED },
                bannerImageResId = profile.bannerImageResId,
                profileImageResId = profile.profileImageResId,
                friends = friends,
                posts = posts
            ),
            onSettingsClick = {},
            onLikeClick = {},
            onUpdatePostTitle = { _, _ -> },
            onDeletePost = {},
            onOpenUserProfile = {},
            onToggleFriend = {}
        )
    }
}

