package com.closify.myapplication.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.domain.model.Outfit
import androidx.compose.ui.tooling.preview.Preview
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
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

    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is HomeNavigationEffect.NavigateToOutfitResult ->
                    onNavigateToOutfitResult(effect.outfits)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
        // TODO: agregar BottomNavBar aquí (próximo PR)
    ) { innerPadding ->
        HomeContent(
            username = uiState.username,
            selectedWeather = uiState.selectedWeather,
            selectedOccasion = uiState.selectedOccasion,
            isAutoWeather = uiState.isAutoWeather,
            isLoadingWeather = uiState.isLoadingWeather,
            isGenerateEnabled = uiState.isGenerateEnabled,
            showNoGarmentsDialog = uiState.showNoGarmentsDialog,
            onEvent = onEvent,
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
                isGenerateEnabled = true
            ),
            onEvent = {}
        )
    }
}
