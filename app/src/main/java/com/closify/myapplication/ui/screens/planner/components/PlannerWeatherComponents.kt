package com.closify.myapplication.ui.screens.planner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.domain.model.PlannerForecastDay

@Composable
internal fun WeatherInfoRow(
    forecast: PlannerForecastDay,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeatherIcon(weather = forecast.weather, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))

        val weatherLabelResId = when (forecast.weather) {
            WeatherCondition.HOT -> R.string.weather_hot_long
            WeatherCondition.COLD -> R.string.weather_cold
            WeatherCondition.WINDY -> R.string.weather_windy
            WeatherCondition.MILD -> R.string.weather_mild
            else -> R.string.weather_any
        }

        Text(
            text = "${forecast.temperature}° ${stringResource(weatherLabelResId)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun WeatherIcon(
    weather: WeatherCondition,
    modifier: Modifier = Modifier
) {
    when (weather) {
        WeatherCondition.MILD -> MildWeatherIcon(modifier = modifier)
        WeatherCondition.ANY  -> Unit
        else -> {
            Icon(
                imageVector = when (weather) {
                    WeatherCondition.HOT -> Icons.Filled.WbSunny
                    WeatherCondition.COLD -> Icons.Filled.AcUnit
                    WeatherCondition.WINDY -> Icons.Filled.Air
                    else -> Icons.Rounded.Cloud
                },
                contentDescription = null,
                tint = when (weather) {
                    WeatherCondition.HOT -> Color(0xFFFFD600)
                    WeatherCondition.COLD -> Color(0xFF90CAF9)
                    WeatherCondition.WINDY -> Color(0xFF64B5F6)
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                modifier = modifier.size(20.dp)
            )
        }
    }
}

@Composable
internal fun MildWeatherIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.WbSunny,
            contentDescription = null,
            tint = Color(0xFFFFD600),
            modifier = Modifier.align(Alignment.TopStart).size(15.dp)
        )
        Icon(
            imageVector = Icons.Rounded.Cloud,
            contentDescription = null,
            tint = Color(0xFF1E88E5),
            modifier = Modifier.align(Alignment.BottomEnd).size(16.dp)
        )
    }
}
