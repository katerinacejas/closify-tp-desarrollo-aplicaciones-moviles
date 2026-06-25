package com.closify.myapplication.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyTopBar
import com.closify.myapplication.ui.location.rememberDeviceLocationRequester
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.HomeEvent
import com.closify.myapplication.ui.viewmodel.HomeNavigationEffect
import com.closify.myapplication.ui.viewmodel.HomeUiState
import com.closify.myapplication.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToOutfitResult: (List<Outfit>) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val requestDeviceLocation = rememberDeviceLocationRequester(
        onLocationAvailable = { location ->
            viewModel.onEvent(HomeEvent.LoadAutomaticWeather(location))
        },
        onLocationUnavailable = {
            viewModel.onEvent(HomeEvent.AutomaticWeatherUnavailable)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is HomeNavigationEffect.NavigateToOutfitResult ->
                    onNavigateToOutfitResult(effect.outfits)
            }
        }
    }

    LaunchedEffect(Unit) {
        requestDeviceLocation()
    }

    HomeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onAutomaticWeatherRequested = requestDeviceLocation
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onAutomaticWeatherRequested: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ClosifyTopBar()
        }
    ) { innerPadding ->
        HomeContent(
            username = uiState.username,
            selectedWeather = uiState.selectedWeather,
            selectedOccasion = uiState.selectedOccasion,
            isAutoWeather = uiState.isAutoWeather,
            isAutoWeatherAvailable = uiState.isAutoWeatherAvailable,
            isLoadingWeather = uiState.isLoadingWeather,
            isGenerateEnabled = uiState.isGenerateEnabled,
            dialog = uiState.dialog,
            onEvent = onEvent,
            onAutomaticWeatherRequested = onAutomaticWeatherRequested,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    ClosifyTheme {
        HomeScreen(
            uiState = HomeUiState(
                username = "Katerina",
                selectedWeather = WeatherCondition.MILD,
                selectedOccasion = Occasion.CASUAL,
                isAutoWeather = false,
                isLoadingWeather = false,
                isGenerateEnabled = true
            ),
            onEvent = {}
        )
    }
}
