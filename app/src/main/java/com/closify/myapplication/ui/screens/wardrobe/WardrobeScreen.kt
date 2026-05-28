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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.screens.wardrobe.components.GarmentGrid
import com.closify.myapplication.ui.screens.wardrobe.components.WardrobeHeader
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.WardrobeEvent
import com.closify.myapplication.ui.viewmodel.WardrobeFilter
import com.closify.myapplication.ui.viewmodel.WardrobeUiState
import com.closify.myapplication.ui.viewmodel.WardrobeViewModel

@Composable
fun WardrobeScreen(
    onCategoryClick: (GarmentCategory) -> Unit,
    onWeatherClick: (WeatherCondition) -> Unit,
    onOccasionClick: (Occasion) -> Unit,
    onGarmentClick: (String) -> Unit,
    viewModel: WardrobeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WardrobeContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onCategoryClick = onCategoryClick,
        onWeatherClick = onWeatherClick,
        onOccasionClick = onOccasionClick,
        onGarmentClick = onGarmentClick
    )
}

@Composable
fun WardrobeContent(
    uiState: WardrobeUiState,
    onEvent: (WardrobeEvent) -> Unit,
    onCategoryClick: (GarmentCategory) -> Unit,
    onWeatherClick: (WeatherCondition) -> Unit,
    onOccasionClick: (Occasion) -> Unit,
    onGarmentClick: (String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WardrobeHeader(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { onEvent(WardrobeEvent.SearchQueryChanged(it)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .then(
                    if (uiState.selectedFilter != WardrobeFilter.ALL) {
                        Modifier.verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Guardarropas",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explorá tus prendas en estantes de tu guardarropa según tipo, clima u ocasión",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SelectableChip(
                    label = "Tipo",
                    selected = uiState.selectedFilter == WardrobeFilter.TYPE,
                    onClick = { onEvent(WardrobeEvent.FilterSelected(WardrobeFilter.TYPE)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SelectableChip(
                    label = "Clima",
                    selected = uiState.selectedFilter == WardrobeFilter.WEATHER,
                    onClick = { onEvent(WardrobeEvent.FilterSelected(WardrobeFilter.WEATHER)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SelectableChip(
                    label = "Ocasión",
                    selected = uiState.selectedFilter == WardrobeFilter.OCCASION,
                    onClick = { onEvent(WardrobeEvent.FilterSelected(WardrobeFilter.OCCASION)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SelectableChip(
                    label = "Todo",
                    selected = uiState.selectedFilter == WardrobeFilter.ALL,
                    onClick = { onEvent(WardrobeEvent.FilterSelected(WardrobeFilter.ALL)) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.selectedFilter == WardrobeFilter.ALL) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Todo tu guardarropas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total ${uiState.allGarments.size} prendas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GarmentGrid(
                    garments = uiState.filteredGarments,
                    onGarmentClick = { onGarmentClick(it.id) }
                )
            } else {
                val shelfTitle = when (uiState.selectedFilter) {
                    WardrobeFilter.TYPE -> "Estantes por tipo de prenda"
                    WardrobeFilter.WEATHER -> "Estantes por clima"
                    WardrobeFilter.OCCASION -> "Estantes por ocasión"
                    else -> "Estantes"
                }

                Text(
                    text = shelfTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState.selectedFilter) {
                    WardrobeFilter.TYPE -> {
                        ShelfList(
                            categoryCounts = uiState.categoryCounts,
                            onCategoryClick = onCategoryClick
                        )
                    }
                    WardrobeFilter.WEATHER -> {
                        WeatherShelfList(
                            weatherCounts = uiState.weatherCounts,
                            onWeatherClick = onWeatherClick
                        )
                    }
                    WardrobeFilter.OCCASION -> {
                        OccasionShelfList(
                            occasionCounts = uiState.occasionCounts,
                            onOccasionClick = onOccasionClick
                        )
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ShelfList(
    categoryCounts: Map<GarmentCategory, Int>,
    onCategoryClick: (GarmentCategory) -> Unit
) {
    val items = listOf(
        GarmentCategory.TOP to "Superior",
        GarmentCategory.BOTTOM to "Inferior",
        GarmentCategory.FOOTWEAR to "Calzado",
        GarmentCategory.OUTERWEAR to "Abrigo",
        GarmentCategory.FULL_BODY to "FullBody"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (category, label) ->
            ShelfCard(
                icon = getCategoryIcon(category),
                label = label,
                count = categoryCounts[category] ?: 0,
                color = MaterialTheme.colorScheme.primaryContainer,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun WeatherShelfList(
    weatherCounts: Map<WeatherCondition, Int>,
    onWeatherClick: (WeatherCondition) -> Unit
) {
    val items = listOf(
        WeatherCondition.HOT to "Calor",
        WeatherCondition.COLD to "Frío",
        WeatherCondition.WINDY to "Ventoso",
        WeatherCondition.MILD to "Templado",
        WeatherCondition.ANY to "Indistinto"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (condition, label) ->
            ShelfCard(
                icon = getWeatherIcon(condition),
                label = label,
                count = weatherCounts[condition] ?: 0,
                color = if (condition == WeatherCondition.ANY) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                onClick = { onWeatherClick(condition) }
            )
        }
    }
}

@Composable
fun OccasionShelfList(
    occasionCounts: Map<Occasion, Int>,
    onOccasionClick: (Occasion) -> Unit
) {
    val items = listOf(
        Occasion.PARTY to "Fiesta",
        Occasion.WORK to "Trabajo",
        Occasion.CASUAL to "Paseo",
        Occasion.ACADEMIC to "Académico",
        Occasion.CHILL to "Chill",
        Occasion.ELEGANT to "Elegante",
        Occasion.ANY to "Indistinto"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (occasion, label) ->
            ShelfCard(
                icon = getOccasionIcon(occasion),
                label = label,
                count = occasionCounts[occasion] ?: 0,
                color = if (occasion == Occasion.ANY) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                onClick = { onOccasionClick(occasion) }
            )
        }
    }
}

@Composable
fun ShelfCard(
    icon: ImageVector,
    label: String,
    count: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$count prendas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCategoryIcon(category: GarmentCategory): ImageVector = when (category) {
    GarmentCategory.TOP -> Icons.Default.ArrowUpward
    GarmentCategory.BOTTOM -> Icons.Default.ArrowDownward
    GarmentCategory.FOOTWEAR -> Icons.Default.RadioButtonUnchecked
    GarmentCategory.OUTERWEAR -> Icons.Default.CheckBoxOutlineBlank
    GarmentCategory.FULL_BODY -> Icons.Default.Person
}

private fun getWeatherIcon(condition: WeatherCondition): ImageVector = when (condition) {
    WeatherCondition.HOT -> Icons.Default.WbSunny
    WeatherCondition.COLD -> Icons.Default.AcUnit
    WeatherCondition.WINDY -> Icons.Default.Air
    WeatherCondition.MILD -> Icons.Default.WbCloudy
    WeatherCondition.ANY -> Icons.Default.AllInclusive
}

private fun getOccasionIcon(occasion: Occasion): ImageVector = when (occasion) {
    Occasion.PARTY -> Icons.Default.NightsStay
    Occasion.WORK -> Icons.Default.Work
    Occasion.CASUAL -> Icons.Default.Explore
    Occasion.ACADEMIC -> Icons.Default.Bookmark
    Occasion.CHILL -> Icons.Default.MusicNote
    Occasion.ELEGANT -> Icons.Default.Sell
    Occasion.ANY -> Icons.Default.Hexagon
}

@Preview(showSystemUi = true)
@Composable
private fun WardrobeScreenPreview() {
    ClosifyTheme {
        WardrobeContent(
            uiState = WardrobeUiState(
                categoryCounts = mapOf(
                    GarmentCategory.TOP to 9,
                    GarmentCategory.BOTTOM to 0,
                    GarmentCategory.FOOTWEAR to 3,
                    GarmentCategory.OUTERWEAR to 2,
                    GarmentCategory.FULL_BODY to 6
                )
            ),
            onEvent = {},
            onCategoryClick = {},
            onWeatherClick = {},
            onOccasionClick = {},
            onGarmentClick = {}
        )
    }
}
