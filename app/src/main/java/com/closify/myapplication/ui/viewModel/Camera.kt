package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.TelemetryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class CameraMode { GALLERY, CAMERA }

data class CameraUiState(
    val selectedMode: CameraMode = CameraMode.GALLERY,
    val selectedImageUri: String = "",
    val hasPermission: Boolean = false,
    val isCapturing: Boolean = false
)

sealed interface CameraEvent {
    data class SelectMode(val mode: CameraMode) : CameraEvent
    data class SetImageUri(val uri: String) : CameraEvent
    data class PermissionResult(val granted: Boolean) : CameraEvent
    data object CaptureStarted : CameraEvent
    data object CaptureFinished : CameraEvent
}

class CameraViewModel(
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.SelectMode       -> _uiState.update { it.copy(selectedMode = event.mode) }
            is CameraEvent.SetImageUri      -> {
                if (event.uri.isNotBlank()) {
                    analyticsTracker.track(
                        AnalyticsEvents.garmentInputSelected(_uiState.value.selectedMode.name)
                    )
                }
                _uiState.update { it.copy(selectedImageUri = event.uri) }
            }
            is CameraEvent.PermissionResult -> _uiState.update { it.copy(hasPermission = event.granted) }
            is CameraEvent.CaptureStarted   -> _uiState.update { it.copy(isCapturing = true) }
            is CameraEvent.CaptureFinished  -> _uiState.update { it.copy(isCapturing = false) }
        }
    }
}
