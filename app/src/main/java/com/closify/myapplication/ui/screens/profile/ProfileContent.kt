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
import com.closify.myapplication.ui.screens.profile.components.*
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewModel.ProfileFriend
import com.closify.myapplication.ui.viewModel.ProfileOutfit
import com.closify.myapplication.ui.viewModel.ProfileUiState

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onSettingsClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFriendsDialog by remember { mutableStateOf(false) }

    if (showFriendsDialog) {
        FriendsDialog(
            friends = uiState.friends,
            profileImageResId = uiState.profileImageResId,
            onDismiss = { showFriendsDialog = false }
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

        items(uiState.outfits) { outfit ->
            ProfileOutfitCard(
                outfit = outfit,
                onLikeClick = { onLikeClick(outfit.id) },
                onCommentsClick = {},
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
                    ProfileFriend("1", "Ayelen Martinez", "@aye_martinez"),
                    ProfileFriend("2", "Milagros Fava", "@miliifava"),
                    ProfileFriend("3", "Ailen Garcia", "@ailu_garcia"),
                    ProfileFriend("4", "Ayelen Balmaceda", "@ayee_balmaceda_1"),
                    ProfileFriend("5", "Camila Martinez", "@camii_martinez")
                ),
                outfits = listOf(
                    ProfileOutfit(
                        id = "1",
                        title = "Mi outfit para mi cumpleanos! <3",
                        date = "Anadido a favoritos el: 25 de mayo de 2026",
                        likes = 3,
                        comments = 2,
                        isLiked = true,
                        garments = listOf("blusa_1", "jean_1", "zapatillas_blancas")
                    ),
                    ProfileOutfit(
                        id = "2",
                        title = "El outfit que me voy a poner en el cumple de mi novio <3",
                        date = "Planificado para el dia: 25 de diciembre de 2026",
                        likes = 5,
                        comments = 1,
                        garments = listOf("vestido_floral", "botas_negras")
                    )
                )
            ),
            onSettingsClick = {},
            onLikeClick = {}
        )
    }
}

