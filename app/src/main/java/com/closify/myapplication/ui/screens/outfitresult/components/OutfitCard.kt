package com.closify.myapplication.ui.screens.outfitresult.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.ui.theme.LavandaAccent

@Composable
fun OutfitCard(
    outfit: Outfit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {

        // Card sin el botón adentro
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp), // espacio para que el botón pueda sobresalir arriba
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                val context = LocalContext.current
                outfit.garments.forEach { garment ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(garment.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = garment.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }

        // Botón superpuesto en el borde superior derecho de la card
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-4).dp, y = 0.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isFavorite) MaterialTheme.colorScheme.primary
                    else LavandaAccent
                )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite
                              else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos"
                                     else "Agregar a favoritos",
                tint = if (isFavorite) Color.White
                       else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
