package com.closify.myapplication.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.SelectableChip

private val weatherOptions = listOf(
    WeatherCondition.HOT   to "Calor",
    WeatherCondition.COLD  to "Frío",
    WeatherCondition.WINDY to "Ventoso",
    WeatherCondition.MILD  to "Templado",
    WeatherCondition.ANY   to "Indistinto"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherSection(
    selectedWeather: WeatherCondition?,
    isAutoWeather: Boolean,
    isLoadingWeather: Boolean,
    onWeatherSelected: (WeatherCondition) -> Unit,
    onToggleAuto: (Boolean) -> Unit
) {
    SectionCard(title = "CLIMA") {

        AutoManualToggle(
            isAuto = isAutoWeather,
            onToggle = onToggleAuto
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoadingWeather) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                weatherOptions.forEach { (condition, label) ->
                    SelectableChip(
                        label = label,
                        selected = selectedWeather == condition,
                        onClick = { onWeatherSelected(condition) },
                        enabled = !isAutoWeather
                    )
                }
            }
        }
    }
}
