package com.closify.myapplication.ui.screens.addgarment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.data.repository.GarmentImageRepositoryImpl
import com.closify.myapplication.ui.components.ClosifyConfirmationDialog
import com.closify.myapplication.ui.components.ClosifyTopBar
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentEvent
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentViewModel
import com.closify.myapplication.ui.viewmodel.ClassifyStep

@Composable
fun ClassifyGarmentScreen(
    imageUri: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: ClassifyGarmentViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClassifyGarmentViewModel(
                    imageUri = imageUri,
                    garmentImageRepository = GarmentImageRepositoryImpl.instance
                ) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ClosifyTopBar(
                showBackButton = true,
                onBackClick = {
                    if (uiState.step == ClassifyStep.BASIC) onBack()
                    else viewModel.onEvent(ClassifyGarmentEvent.Back)
                }
            )
        }
    ) { innerPadding ->
        when (uiState.step) {
            ClassifyStep.BASIC -> ClassifyGarmentStep1Content(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onCancel = onBack,
                modifier = Modifier.padding(innerPadding)
            )
            ClassifyStep.OCCASION,
            ClassifyStep.SAVED -> ClassifyGarmentStep2Content(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onCancel = onBack,
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (uiState.step == ClassifyStep.SAVED) {
            ClosifyConfirmationDialog(
                title = "¡Prenda guardada!",
                subtitle = "Podés verla en tu guardarropa",
                imageUri = uiState.imageUri,
                onDismiss = onSaved
            )
        }

        if (uiState.isProcessingImage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Procesando imagen...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
