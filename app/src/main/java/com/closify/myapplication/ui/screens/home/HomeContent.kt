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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import com.closify.myapplication.R
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.CurrentWeatherSummary
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
    automaticWeatherSummary: CurrentWeatherSummary?,
    selectedOccasion: Occasion?,
    isAutoWeather: Boolean,
    isAutoWeatherAvailable: Boolean,
    isLoadingWeather: Boolean,
    isGenerateEnabled: Boolean,
    dialog: HomeDialog? = null,
    onEvent: (HomeEvent) -> Unit,
    onAutomaticWeatherRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (dialog) {
        HomeDialog.NO_GARMENTS -> ClosifyConfirmationDialog(
            title = stringResource(R.string.dialog_no_garments_title),
            subtitle = stringResource(R.string.dialog_no_garments_subtitle),
            buttonText = stringResource(R.string.btn_continue),
            onDismiss = { onEvent(HomeEvent.DismissDialog) }
        )
        HomeDialog.NO_COMBINATIONS -> ClosifyConfirmationDialog(
            title = stringResource(R.string.dialog_no_combinations_title),
            subtitle = stringResource(R.string.dialog_no_combinations_subtitle),
            buttonText = stringResource(R.string.btn_continue),
            onDismiss = { onEvent(HomeEvent.DismissDialog) }
        )
        HomeDialog.WEATHER_UNAVAILABLE -> ClosifyConfirmationDialog(
            title = stringResource(R.string.dialog_weather_unavailable_title),
            subtitle = stringResource(R.string.dialog_weather_unavailable_subtitle),
            buttonText = stringResource(R.string.btn_continue),
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
                    append(stringResource(R.string.home_good_morning))
                    if (username.isNotEmpty()) append(", ${username.removePrefix("@").uppercase()}")
                }
            },
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.home_what_outfit),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherSection(
            selectedWeather = selectedWeather,
            automaticWeatherSummary = automaticWeatherSummary,
            isAutoWeather = isAutoWeather,
            isAutoWeatherAvailable = isAutoWeatherAvailable,
            isLoadingWeather = isLoadingWeather,
            onWeatherSelected = { onEvent(HomeEvent.SelectWeather(it)) },
            onAutomaticWeatherRequested = onAutomaticWeatherRequested,
            onManualWeatherSelected = { onEvent(HomeEvent.SelectManualWeatherMode) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OccasionSection(
            selectedOccasion = selectedOccasion,
            onOccasionSelected = { onEvent(HomeEvent.SelectOccasion(it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ClosifyButton(
            text = stringResource(R.string.home_generate_button),
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
            automaticWeatherSummary = CurrentWeatherSummary(
                condition = WeatherCondition.MILD,
                averageTemperature = 18,
                minTemperature = 12,
                maxTemperature = 23
            ),
            selectedOccasion = Occasion.CASUAL,
            isAutoWeather = false,
            isAutoWeatherAvailable = true,
            isLoadingWeather = false,
            isGenerateEnabled = true,
            onEvent = {},
            onAutomaticWeatherRequested = {}
        )
    }
}
