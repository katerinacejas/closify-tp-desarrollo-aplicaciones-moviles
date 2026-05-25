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
import com.closify.myapplication.ui.viewmodel.HomeNavigationEffect
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
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
