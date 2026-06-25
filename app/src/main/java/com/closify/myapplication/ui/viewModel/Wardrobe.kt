package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WardrobeUiState(
    val searchQuery: String = "",
    val selectedFilter: WardrobeFilter = WardrobeFilter.TYPE,
    val categoryCounts: Map<GarmentCategory, Int> = emptyMap(),
    val weatherCounts: Map<WeatherCondition, Int> = emptyMap(),
    val occasionCounts: Map<Occasion, Int> = emptyMap(),
    val allGarments: List<Garment> = emptyList(),
    val filteredGarments: List<Garment> = emptyList(),
    val selectedGarment: Garment? = null,
    val isLoading: Boolean = false
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
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    init {
        val userId = userRepository.currentUserId
        garmentRepository.observeGarments(userId)
            .onEach { garments -> onGarmentsUpdated(garments) }
            .launchIn(viewModelScope)
        viewModelScope.launch {
            garmentRepository.syncFromFirestore(userId)
        }
    }

    fun refresh() {
        val userId = userRepository.currentUserId
        viewModelScope.launch {
            garmentRepository.syncFromFirestore(userId)
        }
    }

    private fun onGarmentsUpdated(garments: List<Garment>) {
        _uiState.update {
            it.copy(
                allGarments = garments,
                categoryCounts = garments.groupBy { g -> g.category }.mapValues { e -> e.value.size },
                weatherCounts = WeatherCondition.entries.associateWith { w ->
                    garments.count { g -> w in g.suitableWeather || WeatherCondition.ANY in g.suitableWeather }
                },
                occasionCounts = Occasion.entries.associateWith { o ->
                    garments.count { g -> o in g.suitableOccasions || Occasion.ANY in g.suitableOccasions }
                }
            )
        }
        if (_uiState.value.selectedFilter == WardrobeFilter.ALL) filterGarments()
    }

    fun onEvent(event: WardrobeEvent) {
        when (event) {
            is WardrobeEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query, selectedFilter = WardrobeFilter.ALL) }
                filterGarments()
            }
            is WardrobeEvent.FilterSelected -> {
                analyticsTracker.track(AnalyticsEvents.wardrobeFilterSelected(event.filter.name))
                _uiState.update { it.copy(selectedFilter = event.filter) }
                if (event.filter == WardrobeFilter.ALL) filterGarments()
            }
            is WardrobeEvent.DeleteGarment -> deleteGarment(event.garmentId)
        }
    }

    fun loadGarmentsByCategory(category: GarmentCategory) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            _uiState.update { it.copy(filteredGarments = garmentRepository.getByCategory(category, userId)) }
        }
    }

    fun loadGarmentsByWeather(condition: WeatherCondition) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            _uiState.update { it.copy(filteredGarments = garmentRepository.getByWeather(condition, userId)) }
        }
    }

    fun loadGarmentsByOccasion(occasion: Occasion) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            _uiState.update { it.copy(filteredGarments = garmentRepository.getByOccasion(occasion, userId)) }
        }
    }

    fun getGarmentById(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedGarment = garmentRepository.getById(id)) }
        }
    }

    private fun deleteGarment(id: String) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            runCatching {
                garmentRepository.deleteGarment(id, userId)
            }.onSuccess {
                analyticsTracker.track(AnalyticsEvents.garmentDeleted())
                _uiState.update { state ->
                    state.copy(
                        filteredGarments = state.filteredGarments.filterNot { it.id == id },
                        selectedGarment = null
                    )
                }
            }.onFailure { error ->
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf(
                        "feature" to "garments",
                        "operation" to "delete"
                    )
                )
            }
        }
    }

    private fun filterGarments() {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            val results = garmentRepository.searchByName(_uiState.value.searchQuery, userId)
            _uiState.update { it.copy(filteredGarments = results) }
        }
    }
}
