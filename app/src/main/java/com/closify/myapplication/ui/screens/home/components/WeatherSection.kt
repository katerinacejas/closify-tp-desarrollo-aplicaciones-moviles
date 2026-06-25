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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.SelectableChip

private val weatherOptions = listOf(
    WeatherCondition.HOT   to R.string.weather_hot,
    WeatherCondition.COLD  to R.string.weather_cold,
    WeatherCondition.WINDY to R.string.weather_windy,
    WeatherCondition.MILD  to R.string.weather_mild,
    WeatherCondition.ANY   to R.string.weather_any
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherSection(
    selectedWeather: WeatherCondition?,
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
                weatherOptions.forEach { (condition, labelRes) ->
                    SelectableChip(
                        label = stringResource(labelRes),
                        selected = selectedWeather == condition,
                        onClick = { onWeatherSelected(condition) },
                        enabled = !isAutoWeather
                    )
                }
            }
        }
    }
}
