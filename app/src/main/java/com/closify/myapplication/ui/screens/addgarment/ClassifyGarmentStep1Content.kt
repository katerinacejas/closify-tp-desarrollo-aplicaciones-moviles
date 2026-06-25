package com.closify.myapplication.ui.screens.addgarment

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.screens.addgarment.components.ClassifyGarmentHeader
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentEvent
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentUiState

private val categoryOptions = listOf(
    GarmentCategory.TOP       to R.string.category_top,
    GarmentCategory.BOTTOM    to R.string.category_bottom,
    GarmentCategory.FOOTWEAR  to R.string.category_footwear,
    GarmentCategory.OUTERWEAR to R.string.category_outerwear,
    GarmentCategory.FULL_BODY to R.string.category_full_body
)

private val weatherOptions = listOf(
    WeatherCondition.HOT   to R.string.weather_hot,
    WeatherCondition.COLD  to R.string.weather_cold,
    WeatherCondition.WINDY to R.string.weather_windy,
    WeatherCondition.MILD  to R.string.weather_mild,
    WeatherCondition.ANY   to R.string.weather_any
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassifyGarmentStep1Content(
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
        ClassifyGarmentHeader(imageUri = uiState.imageUri)

        // Nombre
        ClosifyTextField(
            value = uiState.name,
            onValueChange = { onEvent(ClassifyGarmentEvent.NameChanged(it)) },
            placeholder = stringResource(R.string.classify_name_placeholder),
            error = uiState.nameError
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.classify_type_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { (category, labelRes) ->
                        SelectableChip(
                            label = stringResource(labelRes),
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.classify_weather_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weatherOptions.forEach { (weather, labelRes) ->
                        SelectableChip(
                            label = stringResource(labelRes),
                            selected = weather in uiState.selectedWeathers,
                            onClick = { onEvent(ClassifyGarmentEvent.ToggleWeather(weather)) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(R.string.common_cancel),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Button(
                onClick = { onEvent(ClassifyGarmentEvent.Continue) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_continue),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
