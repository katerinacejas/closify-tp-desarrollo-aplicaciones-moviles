package com.closify.myapplication.ui.screens.wardrobe.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.ui.screens.profile.components.ProfileDialogScaffold
import com.closify.myapplication.ui.theme.RosaSecondary

import androidx.compose.ui.tooling.preview.Preview
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.theme.ClosifyTheme

@Composable
fun DeleteGarmentDialog(
    garment: Garment,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ProfileDialogScaffold(
        title = "Eliminar",
        onDismiss = onDismiss,
        heightFraction = null,
        contentTopSpacing = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de la prenda
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .size(160.dp)
                    .border(1.dp, RosaSecondary, RoundedCornerShape(32.dp))
            ) {
                val context = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(garment.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = garment.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¿Estás seguro de eliminar esta prenda de tu guardarropa?",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Esta acción no se puede deshacer.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosaSecondary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Eliminar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteGarmentDialogPreview() {
    ClosifyTheme {
        DeleteGarmentDialog(
            garment = Garment(
                id = "1",
                name = "Blusa manga corta con volados",
                category = GarmentCategory.TOP,
                imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_1",
                suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD),
                suitableOccasions = emptySet()
            ),
            onCancelClick = {},
            onConfirmClick = {},
            onDismiss = {}
        )
    }
}
