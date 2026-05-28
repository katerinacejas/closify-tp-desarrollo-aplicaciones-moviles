package com.closify.myapplication.ui.screens.addgarment

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.components.ClosifyConfirmationDialog
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentEvent
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentViewModel
import com.closify.myapplication.ui.viewmodel.ClassifyStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassifyGarmentScreen(
    imageUri: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ClassifyGarmentViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClassifyGarmentViewModel(imageUri) as T
            }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { ClosifyLogo(size = 48.dp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.step == ClassifyStep.BASIC) onBack()
                        else viewModel.onEvent(ClassifyGarmentEvent.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
    }
}
