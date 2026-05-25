package com.closify.myapplication.ui.screens.outfitresult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.screens.outfitresult.components.OutfitCard
import com.closify.myapplication.ui.screens.outfitresult.components.SavedFavoritesDialog

@Composable
fun OutfitResultContent(
    outfits: List<Outfit>,
    favoriteIds: Set<String>,
    showSavedDialog: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSaveFavorites: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Cuál preferís usar?",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seleccioná los outfits que más te gusten para guardarlos en tu lista fav!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Lista de outfits
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,  // espacio para que el botón del primer card no se corte
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(outfits) { outfit ->
                OutfitCard(
                    outfit = outfit,
                    isFavorite = outfit.id in favoriteIds,
                    onToggleFavorite = { onToggleFavorite(outfit.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Botón guardar favoritos
        ClosifyButton(
            text = "Guardar favoritos",
            onClick = onSaveFavorites,
            enabled = favoriteIds.isNotEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showSavedDialog) {
        SavedFavoritesDialog(onDismiss = onDismissDialog)
    }
}
