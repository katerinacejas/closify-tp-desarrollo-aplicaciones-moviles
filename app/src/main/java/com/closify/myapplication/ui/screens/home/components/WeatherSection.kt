package com.closify.myapplication.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.CurrentWeatherSummary
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.screens.planner.components.WeatherIcon

private val weatherOptions = listOf(
    WeatherCondition.HOT   to R.string.weather_hot,
    WeatherCondition.COLD  to R.string.weather_cold,
    WeatherCondition.WINDY to R.string.weather_windy,
    WeatherCondition.MILD  to R.string.weather_mild,
    WeatherCondition.ANY   to R.string.weather_any
)

private val WeatherOptionsAreaHeight = 92.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherSection(
    selectedWeather: WeatherCondition?,
    automaticWeatherSummary: CurrentWeatherSummary?,
    isAutoWeather: Boolean,
    isAutoWeatherAvailable: Boolean,
    isLoadingWeather: Boolean,
    onWeatherSelected: (WeatherCondition) -> Unit,
    onAutomaticWeatherRequested: () -> Unit,
    onManualWeatherSelected: () -> Unit
) {
    SectionCard(title = stringResource(R.string.home_weather_title)) {

        if (isAutoWeatherAvailable) {
            AutoManualToggle(
                isAuto = isAutoWeather,
                onToggle = { useAutomaticWeather ->
                    if (useAutomaticWeather) {
                        onAutomaticWeatherRequested()
                    } else {
                        onManualWeatherSelected()
                    }
                },
                labelAuto = stringResource(R.string.home_toggle_auto),
                labelManual = stringResource(R.string.home_toggle_manual)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoadingWeather) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WeatherOptionsAreaHeight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (isAutoWeather && automaticWeatherSummary != null) {
            AutomaticWeatherSummary(
                summary = automaticWeatherSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WeatherOptionsAreaHeight)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WeatherOptionsAreaHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    weatherOptions.forEach { (condition, labelRes) ->
                        SelectableChip(
                            label = stringResource(labelRes),
                            selected = selectedWeather == condition,
                            onClick = { onWeatherSelected(condition) },
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomaticWeatherSummary(
    summary: CurrentWeatherSummary,
    modifier: Modifier = Modifier
) {
    val weatherLabelResId = when (summary.condition) {
        WeatherCondition.HOT -> R.string.weather_hot
        WeatherCondition.COLD -> R.string.weather_cold
        WeatherCondition.WINDY -> R.string.weather_windy
        WeatherCondition.MILD -> R.string.weather_mild
        WeatherCondition.ANY -> R.string.weather_any
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherIcon(
                    weather = summary.condition,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${summary.averageTemperature}° ${stringResource(weatherLabelResId).uppercase()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(
                    R.string.home_weather_min_max,
                    summary.minTemperature,
                    summary.maxTemperature
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
