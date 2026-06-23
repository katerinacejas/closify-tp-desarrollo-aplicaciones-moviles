package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.data.repository.WeatherRepository
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.domain.usecase.GenerateOutfitsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeDialog { NO_GARMENTS, NO_COMBINATIONS }

data class HomeUiState(
    val username: String = "",
    val selectedWeather: WeatherCondition? = null,
    val selectedOccasion: Occasion? = null,
    val isAutoWeather: Boolean = false,
    val isLoadingWeather: Boolean = false,
    val isGenerateEnabled: Boolean = false,
    val dialog: HomeDialog? = null
)

sealed interface HomeEvent {
    data class SelectWeather(val weather: WeatherCondition) : HomeEvent
    data class SelectOccasion(val occasion: Occasion) : HomeEvent
    data class ToggleAutoWeather(val isAuto: Boolean) : HomeEvent
    data object GenerateOutfits : HomeEvent
    data object DismissDialog : HomeEvent
}

sealed interface HomeNavigationEffect {
    data class NavigateToOutfitResult(val outfits: List<Outfit>) : HomeNavigationEffect
}

class HomeViewModel(
    private val generateOutfitsUseCase: GenerateOutfitsUseCase = GenerateOutfitsUseCase(),
    private val outfitRepository: OutfitRepository = OutfitRepository.instance,
    private val weatherRepository: WeatherRepository = WeatherRepository.instance,
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(username = userRepository.currentUsername)
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEffect = Channel<HomeNavigationEffect>()
    val navigationEffect = _navigationEffect.receiveAsFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectWeather      -> selectWeather(event.weather)
            is HomeEvent.SelectOccasion     -> selectOccasion(event.occasion)
            is HomeEvent.ToggleAutoWeather  -> toggleAutoWeather(event.isAuto)
            is HomeEvent.GenerateOutfits -> generateOutfits()
            is HomeEvent.DismissDialog   -> _uiState.update { it.copy(dialog = null) }
        }
    }

    private fun selectWeather(weather: WeatherCondition) {
        _uiState.update {
            it.copy(
                selectedWeather = weather,
                isGenerateEnabled = it.selectedOccasion != null
            )
        }
    }

    private fun selectOccasion(occasion: Occasion) {
        _uiState.update {
            it.copy(
                selectedOccasion = occasion,
                isGenerateEnabled = it.selectedWeather != null
            )
        }
    }

    private fun toggleAutoWeather(isAuto: Boolean) {
        _uiState.update { it.copy(isAutoWeather = isAuto) }

        if (isAuto) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingWeather = true) }
                val weather = weatherRepository.getCurrentWeather()
                _uiState.update {
                    it.copy(
                        selectedWeather = weather,
                        isLoadingWeather = false,
                        isGenerateEnabled = it.selectedOccasion != null
                    )
                }
            }
        } else {
            _uiState.update { it.copy(selectedWeather = null, isGenerateEnabled = false) }
        }
    }

    private fun generateOutfits() {
        val state = _uiState.value
        val weather = state.selectedWeather ?: return
        val occasion = state.selectedOccasion ?: return
        val userId = userRepository.currentUserId

        viewModelScope.launch {
            val allGarments = garmentRepository.getAllByUserId(userId)
            if (allGarments.isEmpty()) {
                _uiState.update { it.copy(dialog = HomeDialog.NO_GARMENTS) }
                return@launch
            }

            val outfits = generateOutfitsUseCase(weather, occasion, userId)
            if (outfits.isEmpty()) {
                _uiState.update { it.copy(dialog = HomeDialog.NO_COMBINATIONS) }
                return@launch
            }

            outfitRepository.currentOutfits = outfits
            _navigationEffect.send(HomeNavigationEffect.NavigateToOutfitResult(outfits))
        }
    }
}
