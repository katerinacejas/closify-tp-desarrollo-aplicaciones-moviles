package com.closify.myapplication.ui.screens.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.theme.RosaSecondary
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentEvent
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentUiState

private val categoryOptions = listOf(
    GarmentCategory.TOP       to "Superior",
    GarmentCategory.BOTTOM    to "Inferior",
    GarmentCategory.FOOTWEAR  to "Calzado",
    GarmentCategory.OUTWEAR   to "Abrigo",
    GarmentCategory.FULL_BODY to "FullBody"
)

private val weatherOptions = listOf(
    WeatherCondition.HOT   to "Calor",
    WeatherCondition.COLD  to "Frío",
    WeatherCondition.WINDY to "Ventoso",
    WeatherCondition.MILD  to "Templado",
    WeatherCondition.ANY   to "Indistinto"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassifyGarmentContent(
    uiState: ClassifyGarmentUiState,
    onEvent: (ClassifyGarmentEvent) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Clasificar prenda",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Completa los detalles de tu nueva prenda para catalogarla en el guardarropas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Imagen seleccionada
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Prenda seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre
        ClosifyTextField(
            value = uiState.name,
            onValueChange = { onEvent(ClassifyGarmentEvent.NameChanged(it)) },
            placeholder = "Nombre de la prenda",
            error = uiState.nameError
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tipo de prenda
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "TIPO DE PRENDA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryOptions.forEach { (category, label) ->
                    SelectableChip(
                        label = label,
                        selected = uiState.selectedCategory == category,
                        onClick = { onEvent(ClassifyGarmentEvent.SelectCategory(category)) }
                    )
                }
            }
            if (uiState.categoryError != null) {
                Text(
                    text = uiState.categoryError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Clima
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "¿EN QUÉ CLIMA LA USARÍAS?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weatherOptions.forEach { (weather, label) ->
                    SelectableChip(
                        label = label,
                        selected = weather in uiState.selectedWeathers,
                        onClick = { onEvent(ClassifyGarmentEvent.ToggleWeather(weather)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosaSecondary,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            ClosifyButton(
                text = "Continuar",
                onClick = { onEvent(ClassifyGarmentEvent.Continue) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
