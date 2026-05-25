package com.closify.myapplication.ui.screens.outfitresult

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.R
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.viewmodel.OutfitResultEvent
import com.closify.myapplication.ui.viewmodel.OutfitResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitResultScreen(
    onBack: () -> Unit,
    viewModel: OutfitResultViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { ClosifyLogo(size = 48.dp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        OutfitResultContent(
            outfits = uiState.outfits,
            favoriteIds = uiState.favoriteIds,
            showSavedDialog = uiState.showSavedDialog,
            onToggleFavorite = { viewModel.onEvent(OutfitResultEvent.ToggleFavorite(it)) },
            onSaveFavorites = { viewModel.onEvent(OutfitResultEvent.SaveFavorites) },
            onDismissDialog = { viewModel.onEvent(OutfitResultEvent.DismissDialog) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
