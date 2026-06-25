package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.data.repository.WeatherRepository as WeatherRepositoryImpl
import com.closify.myapplication.domain.model.DeviceLocation
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.domain.repository.WeatherRepository
import com.closify.myapplication.domain.usecase.GenerateOutfitsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeDialog { NO_GARMENTS, NO_COMBINATIONS, WEATHER_UNAVAILABLE }

data class HomeUiState(
    val username: String = "",
    val selectedWeather: WeatherCondition? = null,
    val selectedOccasion: Occasion? = null,
    val isAutoWeather: Boolean = true,
    val isAutoWeatherAvailable: Boolean = true,
    val isLoadingWeather: Boolean = true,
    val isGenerateEnabled: Boolean = false,
    val dialog: HomeDialog? = null
)

sealed interface HomeEvent {
    data class SelectWeather(val weather: WeatherCondition) : HomeEvent
    data class SelectOccasion(val occasion: Occasion) : HomeEvent
    data class LoadAutomaticWeather(val location: DeviceLocation) : HomeEvent
    data object SelectManualWeatherMode : HomeEvent
    data object AutomaticWeatherUnavailable : HomeEvent
    data object GenerateOutfits : HomeEvent
    data object DismissDialog : HomeEvent
}

sealed interface HomeNavigationEffect {
    data class NavigateToOutfitResult(val outfits: List<Outfit>) : HomeNavigationEffect
}

class HomeViewModel(
    private val generateOutfitsUseCase: GenerateOutfitsUseCase = GenerateOutfitsUseCase(),
    private val outfitRepository: OutfitRepository = OutfitRepository.instance,
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.instance,
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private var automaticWeatherJob: Job? = null

    private val _uiState = MutableStateFlow(
        HomeUiState(username = userRepository.currentUsername)
    )
    
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEffect = Channel<HomeNavigationEffect>()
    val navigationEffect = _navigationEffect.receiveAsFlow()

    init {
        userRepository.currentUser
            .filterNotNull()
            .onEach { user -> _uiState.update { it.copy(username = user.profile.username) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectWeather      -> selectWeather(event.weather)
            is HomeEvent.SelectOccasion     -> selectOccasion(event.occasion)
            is HomeEvent.LoadAutomaticWeather -> loadAutomaticWeather(event.location)
            is HomeEvent.SelectManualWeatherMode -> selectManualWeatherMode()
            is HomeEvent.AutomaticWeatherUnavailable -> handleAutomaticWeatherUnavailable()
            is HomeEvent.GenerateOutfits -> generateOutfits()
            is HomeEvent.DismissDialog   -> _uiState.update { it.copy(dialog = null) }
        }
    }

    private fun selectWeather(weather: WeatherCondition) {
        analyticsTracker.track(AnalyticsEvents.manualWeatherSelected(weather.name))
        _uiState.update {
            it.copy(
                selectedWeather = weather,
                isAutoWeather = false,
                isGenerateEnabled = it.selectedOccasion != null
            )
        }
    }

    private fun selectOccasion(occasion: Occasion) {
        analyticsTracker.track(AnalyticsEvents.occasionSelected(occasion.name))
        _uiState.update {
            it.copy(
                selectedOccasion = occasion,
                isGenerateEnabled = it.selectedWeather != null
            )
        }
    }

    private fun loadAutomaticWeather(location: DeviceLocation) {
        automaticWeatherJob?.cancel()
        automaticWeatherJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedWeather = null,
                    isAutoWeather = true,
                    isAutoWeatherAvailable = true,
                    isLoadingWeather = true,
                    isGenerateEnabled = false
                )
            }

            weatherRepository.getCurrentWeather(location)
                .onSuccess { weather ->
                    _uiState.update {
                        it.copy(
                            selectedWeather = weather,
                            isAutoWeather = true,
                            isAutoWeatherAvailable = true,
                            isLoadingWeather = false,
                            isGenerateEnabled = it.selectedOccasion != null
                        )
                    }
                    analyticsTracker.track(AnalyticsEvents.automaticWeatherLoaded(weather.name))
                }
                .onFailure { error ->
                    handleAutomaticWeatherUnavailable(
                        reason = error.message ?: "weather_request_failed",
                        throwable = error
                    )
                }
        }
    }

    private fun selectManualWeatherMode() {
        automaticWeatherJob?.cancel()
        _uiState.update {
            it.copy(
                selectedWeather = null,
                isAutoWeather = false,
                isLoadingWeather = false,
                isGenerateEnabled = false
            )
        }
    }

    private fun handleAutomaticWeatherUnavailable(
        reason: String = "weather_unavailable",
        throwable: Throwable? = null
    ) {
        automaticWeatherJob?.cancel()
        analyticsTracker.track(AnalyticsEvents.automaticWeatherFailed(reason))
        throwable?.let {
            crashReporter.recordException(
                throwable = it,
                keys = mapOf(
                    "feature" to "weather",
                    "operation" to "current_weather"
                )
            )
        }
        _uiState.update {
            it.copy(
                selectedWeather = null,
                isAutoWeather = false,
                isAutoWeatherAvailable = false,
                isLoadingWeather = false,
                isGenerateEnabled = false,
                dialog = HomeDialog.WEATHER_UNAVAILABLE
            )
        }
    }

    private fun generateOutfits() {
        val state = _uiState.value
        val weather = state.selectedWeather ?: return
        val occasion = state.selectedOccasion ?: return
        val userId = userRepository.currentUserId
        val weatherMode = if (state.isAutoWeather) "automatic" else "manual"

        viewModelScope.launch {
            analyticsTracker.track(
                AnalyticsEvents.outfitGenerationRequested(
                    weather = weather.name,
                    occasion = occasion.name,
                    weatherMode = weatherMode
                )
            )
            runCatching {
                val allGarments = garmentRepository.getAllByUserId(userId)
                if (allGarments.isEmpty()) {
                    analyticsTracker.track(
                        AnalyticsEvents.outfitGenerationFailed(
                            reason = "no_garments",
                            weather = weather.name,
                            occasion = occasion.name
                        )
                    )
                    _uiState.update { it.copy(dialog = HomeDialog.NO_GARMENTS) }
                    return@launch
                }

                val outfits = generateOutfitsUseCase(weather, occasion, userId)
                if (outfits.isEmpty()) {
                    analyticsTracker.track(
                        AnalyticsEvents.outfitGenerationFailed(
                            reason = "no_combinations",
                            weather = weather.name,
                            occasion = occasion.name
                        )
                    )
                    _uiState.update { it.copy(dialog = HomeDialog.NO_COMBINATIONS) }
                    return@launch
                }

                analyticsTracker.track(
                    AnalyticsEvents.outfitGenerated(
                        weather = weather.name,
                        occasion = occasion.name,
                        resultCount = outfits.size
                    )
                )
                outfitRepository.currentOutfits = outfits
                _navigationEffect.send(HomeNavigationEffect.NavigateToOutfitResult(outfits))
            }.onFailure { error ->
                analyticsTracker.track(
                    AnalyticsEvents.outfitGenerationFailed(
                        reason = "unexpected_error",
                        weather = weather.name,
                        occasion = occasion.name
                    )
                )
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf(
                        "feature" to "outfit_generation",
                        "weather" to weather.name,
                        "occasion" to occasion.name
                    )
                )
                _uiState.update { it.copy(dialog = HomeDialog.NO_COMBINATIONS) }
            }
        }
    }
}
