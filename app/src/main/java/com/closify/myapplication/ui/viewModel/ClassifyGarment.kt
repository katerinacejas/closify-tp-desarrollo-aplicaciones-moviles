package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ClassifyGarmentUiState(
    val imageUri: String = "",
    val name: String = "",
    val selectedCategory: GarmentCategory? = null,
    val selectedWeathers: Set<WeatherCondition> = emptySet(),
    val nameError: String? = null,
    val categoryError: String? = null
)

sealed interface ClassifyGarmentEvent {
    data class NameChanged(val value: String) : ClassifyGarmentEvent
    data class SelectCategory(val category: GarmentCategory) : ClassifyGarmentEvent
    data class ToggleWeather(val weather: WeatherCondition) : ClassifyGarmentEvent
    data object Continue : ClassifyGarmentEvent
    data object Cancel : ClassifyGarmentEvent
}

class ClassifyGarmentViewModel(imageUri: String) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassifyGarmentUiState(imageUri = imageUri))
    val uiState: StateFlow<ClassifyGarmentUiState> = _uiState.asStateFlow()

    fun onEvent(event: ClassifyGarmentEvent) {
        when (event) {
            is ClassifyGarmentEvent.NameChanged     -> _uiState.update { it.copy(name = event.value, nameError = null) }
            is ClassifyGarmentEvent.SelectCategory  -> _uiState.update { it.copy(selectedCategory = event.category, categoryError = null) }
            is ClassifyGarmentEvent.ToggleWeather   -> toggleWeather(event.weather)
            is ClassifyGarmentEvent.Continue        -> validate()
            is ClassifyGarmentEvent.Cancel          -> { /* manejado en la Screen */ }
        }
    }

    private fun toggleWeather(weather: WeatherCondition) {
        val current = _uiState.value.selectedWeathers
        val updated = if (weather in current) current - weather else current + weather
        _uiState.update { it.copy(selectedWeathers = updated) }
    }

    private fun validate() {
        val state = _uiState.value
        val nameError = if (state.name.trim().isEmpty()) "El nombre no puede estar vacío." else null
        val categoryError = if (state.selectedCategory == null) "Seleccioná un tipo de prenda." else null
        _uiState.update { it.copy(nameError = nameError, categoryError = categoryError) }
        // TODO: si válido → guardar prenda y navegar
    }
}
