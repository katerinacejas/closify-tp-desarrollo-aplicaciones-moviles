package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.OutfitRepository
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
    private val outfitRepository: OutfitRepository = OutfitRepository.instance
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
        outfitRepository.saveFavorites(state.outfits, state.outfitNames)
        _uiState.update { it.copy(showSavedDialog = true) }
    }
}
