package com.closify.myapplication.ui.screens.wardrobe

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.closify.myapplication.ui.screens.wardrobe.components.DeleteGarmentDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.screens.wardrobe.components.WardrobeHeader
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.WardrobeEvent
import com.closify.myapplication.ui.viewmodel.WardrobeViewModel

@Composable
fun GarmentDetailScreen(
    garmentId: String,
    onBack: () -> Unit,
    viewModel: WardrobeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(garmentId) {
        viewModel.getGarmentById(garmentId)
    }

    val garment = uiState.selectedGarment

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WardrobeHeader(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { viewModel.onEvent(WardrobeEvent.SearchQueryChanged(it)) }
            )
        }
    ) { innerPadding ->
        if (garment != null) {
            GarmentDetailContent(
                garment = garment,
                onBack = onBack,
                onDelete = { showDeleteDialog = true },
                modifier = Modifier.padding(innerPadding)
            )

            if (showDeleteDialog) {
                DeleteGarmentDialog(
                    garment = garment,
                    onCancelClick = { showDeleteDialog = false },
                    onDismiss = { showDeleteDialog = false },
                    onConfirmClick = {
                        showDeleteDialog = false
                        viewModel.onEvent(WardrobeEvent.DeleteGarment(garment.id))
                        onBack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GarmentDetailContent(
    garment: Garment,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = stringResource(R.string.garment_detail_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(end = 48.dp) // Offset for centered title
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(32.dp))
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
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = garment.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                DetailSection(title = stringResource(R.string.garment_detail_type)) {
                    SelectableChip(
                        label = stringResource(getCategoryLabelRes(garment.category)),
                        selected = true,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                DetailSection(title = stringResource(R.string.garment_detail_weather)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        garment.suitableWeather.filter { it != WeatherCondition.ANY }.forEach { weather ->
                            SelectableChip(
                                label = stringResource(getWeatherLabelRes(weather)),
                                selected = true,
                                onClick = {}
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                DetailSection(title = stringResource(R.string.garment_detail_occasion)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        garment.suitableOccasions.filter { it != Occasion.ANY }.forEach { occasion ->
                            SelectableChip(
                                label = stringResource(getOccasionLabelRes(occasion)),
                                selected = true,
                                onClick = {}
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                ClosifyButton(
                    text = stringResource(R.string.garment_detail_delete),
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

private fun getCategoryLabelRes(category: GarmentCategory): Int = when (category) {
    GarmentCategory.TOP -> R.string.category_top
    GarmentCategory.BOTTOM -> R.string.category_bottom
    GarmentCategory.FOOTWEAR -> R.string.category_footwear
    GarmentCategory.OUTERWEAR -> R.string.category_outerwear
    GarmentCategory.FULL_BODY -> R.string.category_full_body
}

private fun getWeatherLabelRes(weather: WeatherCondition): Int = when (weather) {
    WeatherCondition.HOT -> R.string.weather_hot
    WeatherCondition.COLD -> R.string.weather_cold
    WeatherCondition.WINDY -> R.string.weather_windy
    WeatherCondition.MILD -> R.string.weather_mild
    WeatherCondition.ANY -> R.string.weather_any
}

private fun getOccasionLabelRes(occasion: Occasion): Int = when (occasion) {
    Occasion.WORK -> R.string.occasion_work
    Occasion.ACADEMIC -> R.string.occasion_academic
    Occasion.CASUAL -> R.string.occasion_casual
    Occasion.PARTY -> R.string.occasion_party
    Occasion.CHILL -> R.string.occasion_chill
    Occasion.ELEGANT -> R.string.occasion_elegant
    Occasion.ANY -> R.string.occasion_any
}

@Preview(showSystemUi = true)
@Composable
private fun GarmentDetailPreview() {
    val sampleGarment = Garment(
        id = "1",
        name = "Blusa manga corta con volados",
        category = GarmentCategory.TOP,
        imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_1",
        suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD),
        suitableOccasions = setOf(Occasion.WORK, Occasion.ACADEMIC, Occasion.CASUAL)
    )
    ClosifyTheme {
        GarmentDetailContent(
            garment = sampleGarment,
            onBack = {},
            onDelete = {}
        )
    }
}
