package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.domain.model.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OutfitResultUiState(
    val outfits: List<Outfit> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val showSavedDialog: Boolean = false
)

sealed interface OutfitResultEvent {
    data class ToggleFavorite(val outfitId: String) : OutfitResultEvent
    data object SaveFavorites : OutfitResultEvent
    data object DismissDialog : OutfitResultEvent
}

class OutfitResultViewModel(
    private val outfitRepository: OutfitRepository = OutfitRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OutfitResultUiState(outfits = outfitRepository.currentOutfits)
    )
    val uiState: StateFlow<OutfitResultUiState> = _uiState.asStateFlow()

    fun onEvent(event: OutfitResultEvent) {
        when (event) {
            is OutfitResultEvent.ToggleFavorite -> toggleFavorite(event.outfitId)
            is OutfitResultEvent.SaveFavorites  -> saveFavorites()
            is OutfitResultEvent.DismissDialog  -> _uiState.update { it.copy(showSavedDialog = false) }
        }
    }

    private fun toggleFavorite(outfitId: String) {
        val current = _uiState.value.favoriteIds
        val updated = if (outfitId in current) current - outfitId else current + outfitId
        _uiState.update { it.copy(favoriteIds = updated) }
    }

    private fun saveFavorites() {
        val favorites = _uiState.value.outfits.filter {
            it.id in _uiState.value.favoriteIds
        }
        outfitRepository.saveFavorites(favorites)
        _uiState.update { it.copy(showSavedDialog = true) }
    }
}
