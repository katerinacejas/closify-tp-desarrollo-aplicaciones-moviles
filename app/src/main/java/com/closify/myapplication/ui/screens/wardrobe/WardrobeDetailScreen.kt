package com.closify.myapplication.ui.screens.wardrobe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.screens.wardrobe.components.GarmentGrid
import com.closify.myapplication.ui.screens.wardrobe.components.WardrobeHeader
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.WardrobeEvent
import com.closify.myapplication.ui.viewmodel.WardrobeUiState
import com.closify.myapplication.ui.viewmodel.WardrobeViewModel

@Composable
fun WardrobeDetailScreen(
    category: GarmentCategory? = null,
    weather: WeatherCondition? = null,
    occasion: Occasion? = null,
    onBack: () -> Unit,
    onGarmentClick: (String) -> Unit,
    viewModel: WardrobeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(category, weather, occasion) {
        category?.let { viewModel.loadGarmentsByCategory(it) }
        weather?.let { viewModel.loadGarmentsByWeather(it) }
        occasion?.let { viewModel.loadGarmentsByOccasion(it) }
    }

    WardrobeDetailContent(
        category = category,
        weather = weather,
        occasion = occasion,
        uiState = uiState,
        onBack = onBack,
        onGarmentClick = onGarmentClick,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun WardrobeDetailContent(
    category: GarmentCategory?,
    weather: WeatherCondition?,
    occasion: Occasion?,
    uiState: WardrobeUiState,
    onBack: () -> Unit,
    onGarmentClick: (String) -> Unit,
    onEvent: (WardrobeEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WardrobeHeader(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { onEvent(WardrobeEvent.SearchQueryChanged(it)) },
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            FilterTitle(category = category, weather = weather, occasion = occasion)

            Spacer(modifier = Modifier.height(24.dp))

            GarmentGrid(
                garments = uiState.filteredGarments,
                onGarmentClick = { onGarmentClick(it.id) }
            )
        }
    }
}

@Composable
fun FilterTitle(category: GarmentCategory?, weather: WeatherCondition?, occasion: Occasion?) {
    val (icon, color, label) = when {
        category != null -> {
            val (i, c, l) = when (category) {
                GarmentCategory.TOP -> Triple(Icons.Default.ArrowUpward, Color(0xFFB9A7F7), "Superior")
                GarmentCategory.BOTTOM -> Triple(Icons.Default.ArrowDownward, Color(0xFFB9A7F7), "Inferior")
                GarmentCategory.FOOTWEAR -> Triple(Icons.Default.RadioButtonUnchecked, Color(0xFFF8BBD0), "Calzado")
                GarmentCategory.OUTERWEAR -> Triple(Icons.Default.CheckBoxOutlineBlank, Color(0xFFF8BBD0), "Abrigo")
                GarmentCategory.FULL_BODY -> Triple(Icons.Default.Person, Color(0xFF7C3AED), "FullBody")
            }
            Triple(i, c, "Estante: $l")
        }
        weather != null -> {
            val (i, c, l) = when (weather) {
                WeatherCondition.HOT -> Triple(Icons.Default.WbSunny, Color(0xFFB9A7F7), "Calor")
                WeatherCondition.COLD -> Triple(Icons.Default.AcUnit, Color(0xFFB9A7F7), "Frío")
                WeatherCondition.WINDY -> Triple(Icons.Default.Air, Color(0xFFB9A7F7), "Ventoso")
                WeatherCondition.MILD -> Triple(Icons.Default.WbCloudy, Color(0xFFB9A7F7), "Templado")
                WeatherCondition.ANY -> Triple(Icons.Default.AllInclusive, Color(0xFF7C3AED), "Indistinto")
            }
            Triple(i, c, "Estante: $l")
        }
        occasion != null -> {
            val (i, c, l) = when (occasion) {
                Occasion.PARTY -> Triple(Icons.Default.NightsStay, Color(0xFFB9A7F7), "Fiesta")
                Occasion.WORK -> Triple(Icons.Default.Work, Color(0xFFB9A7F7), "Trabajo")
                Occasion.CASUAL -> Triple(Icons.Default.Explore, Color(0xFFB9A7F7), "Paseo")
                Occasion.ACADEMIC -> Triple(Icons.Default.Bookmark, Color(0xFFB9A7F7), "Académico")
                Occasion.CHILL -> Triple(Icons.Default.MusicNote, Color(0xFFB9A7F7), "Chill")
                Occasion.ELEGANT -> Triple(Icons.Default.Sell, Color(0xFFB9A7F7), "Elegante")
                Occasion.ANY -> Triple(Icons.Default.Hexagon, Color(0xFF7C3AED), "Indistinto")
            }
            Triple(i, c, "Estante: $l")
        }
        else -> Triple(Icons.Default.AllInclusive, Color.Gray, "Estante")
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun WardrobeDetailPreview() {
    val sampleGarments = listOf(
        Garment(id = "1", name = "Blusa 1", category = GarmentCategory.TOP, imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_1", suitableWeather = emptySet(), suitableOccasions = emptySet()),
        Garment(id = "2", name = "Blusa 2", category = GarmentCategory.TOP, imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_elegante_1", suitableWeather = emptySet(), suitableOccasions = emptySet()),
        Garment(id = "3", name = "Blusa 3", category = GarmentCategory.TOP, imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_elegante_2", suitableWeather = emptySet(), suitableOccasions = emptySet())
    )
    ClosifyTheme {
        WardrobeDetailContent(
            category = GarmentCategory.TOP,
            weather = null,
            occasion = null,
            uiState = WardrobeUiState(allGarments = sampleGarments, filteredGarments = sampleGarments),
            onBack = {},
            onGarmentClick = {},
            onEvent = {}
        )
    }
}
