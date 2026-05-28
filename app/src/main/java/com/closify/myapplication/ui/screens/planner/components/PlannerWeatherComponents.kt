package com.closify.myapplication.ui.screens.planner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.theme.LavandaAccent
import com.closify.myapplication.ui.viewmodel.PlannerForecastDay

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
        Text(
            text = "${forecast.temperature}° ${forecast.label}",
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
            val icon: ImageVector
            val tint: Color
            when (weather) {
                WeatherCondition.HOT   -> { icon = Icons.Rounded.WbSunny;  tint = Color(0xFFFFD600) }
                WeatherCondition.COLD  -> { icon = Icons.Rounded.WaterDrop; tint = Color(0xFF1E88E5) }
                WeatherCondition.WINDY -> { icon = Icons.Rounded.Cloud;     tint = Color(0xFF1E88E5) }
                else                   -> { icon = Icons.Rounded.Cloud;     tint = LavandaAccent    }
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(18.dp)
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
            imageVector = Icons.Rounded.WbSunny,
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
