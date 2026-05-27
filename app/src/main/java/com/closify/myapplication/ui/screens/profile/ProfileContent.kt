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
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.ui.screens.profile.components.*
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewModel.ProfileUiState

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onSettingsClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFriendsDialog by remember { mutableStateOf(false) }
    var selectedLikesOutfitId by remember { mutableStateOf<String?>(null) }
    var selectedCommentsOutfitId by remember { mutableStateOf<String?>(null) }
    val selectedLikesOutfit = uiState.posts.firstOrNull { it.id == selectedLikesOutfitId }
    val selectedCommentsOutfit = uiState.posts.firstOrNull { it.id == selectedCommentsOutfitId }

    if (showFriendsDialog) {
        FriendsDialog(
            friends = uiState.friends,
            onDismiss = { showFriendsDialog = false }
        )
    }

    if (selectedLikesOutfit != null) {
        LikesDialog(
            likes = selectedLikesOutfit.likedBy,
            onDismiss = { selectedLikesOutfitId = null }
        )
    }

    if (selectedCommentsOutfit != null) {
        CommentsDialog(
            comments = selectedCommentsOutfit.comments,
            onDismiss = { selectedCommentsOutfitId = null }
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
                onLikeClick = { onLikeClick(outfit.id) },
                onLikesTextClick = { selectedLikesOutfitId = outfit.id },
                onCommentsClick = { onCommentClick(outfit.id) },
                onCommentsTextClick = { selectedCommentsOutfitId = outfit.id },
                onEditClick = {},
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ProfileContentPreview() {
    ClosifyTheme {
        ProfileContent(
            uiState = ProfileUiState(
                name = "Katerina Cejas",
                username = "@kate_cejas_1999",
                bio = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
                birthDate = "3 de septiembre de 1999",
                friendsCount = 12,
                garmentsCount = 23,
                wardrobeUsagePercentage = 70,
                favoriteOutfitsCount = 2,
                plannedOutfitsCount = 2,
                bannerImageResId = R.drawable.banner_default,
                profileImageResId = R.drawable.avatar_default,
                friends = listOf(
                    UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default),
                    UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default),
                    UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default),
                    UserSummary("4", "Ayelen Balmaceda", "@ayee_balmaceda_1", R.drawable.avatar_default),
                    UserSummary("5", "Camila Martinez", "@camii_martinez", R.drawable.avatar_default)
                ),
                posts = listOf(
                    OutfitPost(
                        id = "1",
                        title = "Mi outfit para mi cumpleanos! <3",
                        type = OutfitPostType.FAVORITE,
                        eventDate = "25 de mayo de 2026",
                        isLiked = true,
                        likedBy = listOf(
                            Like("1", UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default), "25 de mayo de 2026"),
                            Like("2", UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default), "25 de mayo de 2026"),
                            Like("3", UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default), "25 de mayo de 2026")
                        ),
                        comments = listOf(
                            Comment(
                                id = "1",
                                user = UserSummary("6", "Andrea Gonzalez", "@andrea_gonzalez", R.drawable.avatar_default),
                                text = "Ay que lindo outfit amigaaa! Me encanta <3",
                                createdAt = "25 de mayo de 2026"
                            ),
                            Comment(
                                id = "2",
                                user = UserSummary("7", "Agustina Marrapodia", "@agustina_marrapodia", R.drawable.avatar_default),
                                text = "Amigaaaa, te queda hermoso, tenes que prestarme esa blusa :)",
                                createdAt = "25 de mayo de 2026"
                            )
                        ),
                        garmentImageNames = listOf("blusa_1", "jean_1", "zapatillas_blancas")
                    ),
                    OutfitPost(
                        id = "2",
                        title = "El outfit que me voy a poner en el cumple de mi novio <3",
                        type = OutfitPostType.PLANNED,
                        eventDate = "25 de diciembre de 2026",
                        comments = emptyList(),
                        garmentImageNames = listOf("vestido_floral", "botas_negras")
                    )
                )
            ),
            onSettingsClick = {},
            onLikeClick = {},
            onCommentClick = {}
        )
    }
}

