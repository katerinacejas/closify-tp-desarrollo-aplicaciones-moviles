package com.closify.myapplication.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyConfirmationDialog
import com.closify.myapplication.ui.viewmodel.HomeDialog
import com.closify.myapplication.ui.screens.home.components.OccasionSection
import com.closify.myapplication.ui.screens.home.components.WeatherSection
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.HomeEvent

@Composable
fun HomeContent(
    username: String,
    selectedWeather: WeatherCondition?,
    selectedOccasion: Occasion?,
    isAutoWeather: Boolean,
    isLoadingWeather: Boolean,
    isGenerateEnabled: Boolean,
    dialog: HomeDialog? = null,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (dialog) {
        HomeDialog.NO_GARMENTS -> ClosifyConfirmationDialog(
            title = "Tu guardarropa está vacío",
            subtitle = "Agrega prendas en tu guardarropa\npara poder generar outfits",
            buttonText = "Continuar",
            onDismiss = { onEvent(HomeEvent.DismissDialog) }
        )
        HomeDialog.NO_COMBINATIONS -> ClosifyConfirmationDialog(
            title = "Sin combinaciones posibles",
            subtitle = "No encontramos prendas que combinen\ncon el clima y ocasión que elegiste",
            buttonText = "Continuar",
            onDismiss = { onEvent(HomeEvent.DismissDialog) }
        )
        null -> Unit
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("BUEN DÍA")
                    if (username.isNotEmpty()) append(", ${username.removePrefix("@").uppercase()}")
                }
            },
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "¿Qué outfit preferís hoy?",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherSection(
            selectedWeather = selectedWeather,
            isAutoWeather = isAutoWeather,
            isLoadingWeather = isLoadingWeather,
            onWeatherSelected = { onEvent(HomeEvent.SelectWeather(it)) },
            onToggleAuto = { onEvent(HomeEvent.ToggleAutoWeather(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OccasionSection(
            selectedOccasion = selectedOccasion,
            onOccasionSelected = { onEvent(HomeEvent.SelectOccasion(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ClosifyButton(
            text = "Generar outfits",
            onClick = { onEvent(HomeEvent.GenerateOutfits) },
            enabled = isGenerateEnabled
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeContentPreview() {
    ClosifyTheme {
        HomeContent(
            username = "katerina",
            selectedWeather = WeatherCondition.MILD,
            selectedOccasion = Occasion.CASUAL,
            isAutoWeather = false,
            isLoadingWeather = false,
            isGenerateEnabled = true,
            onEvent = {}
        )
    }
}
