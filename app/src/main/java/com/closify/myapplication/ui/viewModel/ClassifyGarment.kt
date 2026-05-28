package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.MockClosifyData
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.domain.usecase.SaveGarmentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

enum class ClassifyStep { BASIC, OCCASION, SAVED }

data class ClassifyGarmentUiState(
    val imageUri: String = "",
    val name: String = "",
    val selectedCategory: GarmentCategory? = null,
    val selectedWeathers: Set<WeatherCondition> = emptySet(),
    val selectedOccasions: Set<Occasion> = emptySet(),
    val nameError: String? = null,
    val categoryError: String? = null,
    val step: ClassifyStep = ClassifyStep.BASIC
)

sealed interface ClassifyGarmentEvent {
    data class NameChanged(val value: String) : ClassifyGarmentEvent
    data class SelectCategory(val category: GarmentCategory) : ClassifyGarmentEvent
    data class ToggleWeather(val weather: WeatherCondition) : ClassifyGarmentEvent
    data class ToggleOccasion(val occasion: Occasion) : ClassifyGarmentEvent
    data object Continue : ClassifyGarmentEvent
    data object Save : ClassifyGarmentEvent
    data object Back : ClassifyGarmentEvent
}

class ClassifyGarmentViewModel(
    imageUri: String,
    private val saveGarmentUseCase: SaveGarmentUseCase = SaveGarmentUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassifyGarmentUiState(imageUri = imageUri))
    val uiState: StateFlow<ClassifyGarmentUiState> = _uiState.asStateFlow()

    fun onEvent(event: ClassifyGarmentEvent) {
        when (event) {
            is ClassifyGarmentEvent.NameChanged    -> _uiState.update { it.copy(name = event.value, nameError = null) }
            is ClassifyGarmentEvent.SelectCategory -> _uiState.update { it.copy(selectedCategory = event.category, categoryError = null) }
            is ClassifyGarmentEvent.ToggleWeather  -> toggleWeather(event.weather)
            is ClassifyGarmentEvent.ToggleOccasion -> toggleOccasion(event.occasion)
            is ClassifyGarmentEvent.Continue       -> validateAndAdvance()
            is ClassifyGarmentEvent.Save           -> save()
            is ClassifyGarmentEvent.Back           -> goBack()
        }
    }

    private fun toggleWeather(weather: WeatherCondition) {
        val current = _uiState.value.selectedWeathers
        _uiState.update { it.copy(selectedWeathers = if (weather in current) current - weather else current + weather) }
    }

    private fun toggleOccasion(occasion: Occasion) {
        val current = _uiState.value.selectedOccasions
        _uiState.update { it.copy(selectedOccasions = if (occasion in current) current - occasion else current + occasion) }
    }

    private fun validateAndAdvance() {
        val state = _uiState.value
        val nameError = if (state.name.trim().isEmpty()) "El nombre no puede estar vacío." else null
        val categoryError = if (state.selectedCategory == null) "Seleccioná un tipo de prenda." else null
        val nextStep = if (nameError == null && categoryError == null) ClassifyStep.OCCASION else ClassifyStep.BASIC
        _uiState.update { it.copy(nameError = nameError, categoryError = categoryError, step = nextStep) }
    }

    private fun goBack() {
        _uiState.update {
            when (it.step) {
                ClassifyStep.OCCASION, ClassifyStep.SAVED -> it.copy(step = ClassifyStep.BASIC)
                ClassifyStep.BASIC -> it
            }
        }
    }

    private fun save() {
        val state = _uiState.value
        val garment = Garment(
            id = UUID.randomUUID().toString(),
            ownerUserId = MockClosifyData.CURRENT_USER_ID,
            name = state.name.trim(),
            category = state.selectedCategory!!,
            imageUrl = state.imageUri,
            suitableWeather = state.selectedWeathers.ifEmpty { setOf(WeatherCondition.ANY) },
            suitableOccasions = state.selectedOccasions.ifEmpty { setOf(Occasion.ANY) },
            createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "AR"))
            )
        )
        saveGarmentUseCase(garment)
        _uiState.update { it.copy(step = ClassifyStep.SAVED) }
    }
}
