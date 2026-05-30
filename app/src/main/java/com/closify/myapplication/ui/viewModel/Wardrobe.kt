package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WardrobeUiState(
    val searchQuery: String = "",
    val selectedFilter: WardrobeFilter = WardrobeFilter.TYPE,
    val categoryCounts: Map<GarmentCategory, Int> = emptyMap(),
    val weatherCounts: Map<WeatherCondition, Int> = emptyMap(),
    val occasionCounts: Map<Occasion, Int> = emptyMap(),
    val allGarments: List<Garment> = emptyList(),
    val filteredGarments: List<Garment> = emptyList(),
    val selectedGarment: Garment? = null,
    val isLoading: Boolean = false,
)

enum class WardrobeFilter {
    TYPE, WEATHER, OCCASION, ALL
}

sealed interface WardrobeEvent {
    data class SearchQueryChanged(val query: String) : WardrobeEvent
    data class FilterSelected(val filter: WardrobeFilter) : WardrobeEvent
    data class DeleteGarment(val garmentId: String) : WardrobeEvent
}

class WardrobeViewModel(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    init {
        loadGarments()
    }

    private fun loadGarments() {
        _uiState.update {
            it.copy(
                categoryCounts = garmentRepository.getCategoryCounts(),
                weatherCounts = garmentRepository.getWeatherCounts(),
                occasionCounts = garmentRepository.getOccasionCounts(),
                allGarments = garmentRepository.getAllByUserId()
            )
        }

        if (_uiState.value.selectedFilter == WardrobeFilter.ALL) {
            filterGarments()
        }
    }

    fun onEvent(event: WardrobeEvent) {
        when (event) {
            is WardrobeEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                filterGarments()
            }

            is WardrobeEvent.FilterSelected -> {
                _uiState.update { it.copy(selectedFilter = event.filter) }
                if (event.filter == WardrobeFilter.ALL) {
                    filterGarments()
                }
            }

            is WardrobeEvent.DeleteGarment -> {
                deleteGarment(event.garmentId)
            }
        }
    }

    fun loadGarmentsByCategory(category: GarmentCategory) {
        _uiState.update { it.copy(filteredGarments = garmentRepository.getByCategory(category)) }
    }

    fun loadGarmentsByWeather(condition: WeatherCondition) {
        _uiState.update { it.copy(filteredGarments = garmentRepository.getByWeather(condition)) }
    }

    fun loadGarmentsByOccasion(occasion: Occasion) {
        _uiState.update { it.copy(filteredGarments = garmentRepository.getByOccasion(occasion)) }
    }

    fun getGarmentById(id: String) {
        _uiState.update { it.copy(selectedGarment = garmentRepository.getById(id)) }
    }

    private fun deleteGarment(id: String) {
        garmentRepository.deleteGarment(id)
        _uiState.update { state ->
            state.copy(
                filteredGarments = state.filteredGarments.filterNot { it.id == id },
                selectedGarment = null
            )
        }
        loadGarments()
    }

    private fun filterGarments() {
        _uiState.update {
            it.copy(filteredGarments = garmentRepository.searchByName(it.searchQuery))
        }
    }
}
