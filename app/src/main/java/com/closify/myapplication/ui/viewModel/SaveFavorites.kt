package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.OutfitRepository
import kotlinx.coroutines.launch
import com.closify.myapplication.domain.model.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SaveFavoritesUiState(
    val outfits: List<Outfit> = emptyList(),
    val outfitNames: Map<String, String> = emptyMap(),
    val showSavedDialog: Boolean = false
)

sealed interface SaveFavoritesEvent {
    data class NameChanged(val outfitId: String, val name: String) : SaveFavoritesEvent
    data object Save : SaveFavoritesEvent
    data object DismissDialog : SaveFavoritesEvent
}

class SaveFavoritesViewModel(
    private val outfitRepository: OutfitRepository = OutfitRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SaveFavoritesUiState(outfits = outfitRepository.pendingFavorites)
    )
    val uiState: StateFlow<SaveFavoritesUiState> = _uiState.asStateFlow()

    fun onEvent(event: SaveFavoritesEvent) {
        when (event) {
            is SaveFavoritesEvent.NameChanged -> _uiState.update {
                it.copy(outfitNames = it.outfitNames + (event.outfitId to event.name))
            }
            is SaveFavoritesEvent.Save        -> save()
            is SaveFavoritesEvent.DismissDialog -> _uiState.update { it.copy(showSavedDialog = false) }
        }
    }

    private fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            runCatching {
                outfitRepository.saveFavorites(state.outfits, state.outfitNames)
            }.onSuccess {
                analyticsTracker.track(AnalyticsEvents.favoriteOutfitsSaved(state.outfits.size))
                _uiState.update { it.copy(showSavedDialog = true) }
            }.onFailure { error ->
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf(
                        "feature" to "favorite_outfits",
                        "operation" to "save",
                        "outfit_count" to state.outfits.size
                    )
                )
            }
        }
    }
}
