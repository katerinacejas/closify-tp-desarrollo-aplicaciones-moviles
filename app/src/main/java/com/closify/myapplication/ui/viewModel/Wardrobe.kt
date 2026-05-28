package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.MockClosifyData
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
        val garments = garmentRepository.getAllByUserId()
        
        // Conteo por Categoría
        val catCounts = garments.groupBy { it.category }
            .mapValues { it.value.size }
        
        // Conteo por Clima
        val weathCounts = mutableMapOf<WeatherCondition, Int>()
        WeatherCondition.entries.forEach { condition ->
            weathCounts[condition] = garmentRepository.getByWeather(condition).size
        }

        // Conteo por Ocasión
        val occCounts = mutableMapOf<Occasion, Int>()
        Occasion.entries.forEach { occasion ->
            occCounts[occasion] = garmentRepository.getByOccasion(occasion).size
        }
        
        _uiState.update { it.copy(
            categoryCounts = catCounts,
            weatherCounts = weathCounts,
            occasionCounts = occCounts,
            allGarments = garments
        ) }

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
        val filtered = garmentRepository.getByCategory(category)
        _uiState.update { it.copy(filteredGarments = filtered) }
    }

    fun loadGarmentsByWeather(condition: WeatherCondition) {
        val filtered = garmentRepository.getByWeather(condition)
        _uiState.update { it.copy(filteredGarments = filtered) }
    }

    fun loadGarmentsByOccasion(occasion: Occasion) {
        val filtered = garmentRepository.getByOccasion(occasion)
        _uiState.update { it.copy(filteredGarments = filtered) }
    }

    fun getGarmentById(id: String) {
        val garment = garmentRepository.getAllByUserId().find { it.id == id }
        _uiState.update { it.copy(selectedGarment = garment) }
    }

    private fun deleteGarment(id: String) {
        MockClosifyData.garments.removeIf { it.id == id }
        
        _uiState.update { state ->
            val updatedFiltered = state.filteredGarments.filter { it.id != id }
            state.copy(
                filteredGarments = updatedFiltered,
                selectedGarment = null
            )
        }
        loadGarments()
    }

    private fun filterGarments() {
        val query = _uiState.value.searchQuery
        val allGarments = garmentRepository.getAllByUserId()
        val filtered = if (query.isEmpty()) {
            allGarments
        } else {
            allGarments.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(filteredGarments = filtered) }
    }
}
