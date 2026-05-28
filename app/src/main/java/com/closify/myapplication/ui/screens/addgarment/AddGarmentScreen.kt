package com.closify.myapplication.ui.screens.addgarment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.viewmodel.CameraEvent
import com.closify.myapplication.ui.viewmodel.CameraMode
import com.closify.myapplication.ui.viewmodel.CameraViewModel

@Composable
fun AddGarmentScreen(
    onNavigateToClassify: () -> Unit,
    viewModel: CameraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(CameraEvent.SetImageUri(it.toString()))
            onNavigateToClassify()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        AddGarmentContent(
            selectedMode = uiState.selectedMode,
            onEvent = viewModel::onEvent,
            onCardClick = {
                when (uiState.selectedMode) {
                    CameraMode.GALLERY -> galleryLauncher.launch(
                        PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                    )
                    CameraMode.CAMERA -> { /* TODO: implementar cámara */ }
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
