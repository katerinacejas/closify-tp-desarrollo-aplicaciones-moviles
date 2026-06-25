package com.closify.myapplication.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.remote.CloudinaryService
import com.closify.myapplication.data.remote.RemoveBgService
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class ClassifyStep { BASIC, OCCASION, SAVED }

data class ClassifyGarmentUiState(
    val imageUri: String = "",
    val name: String = "",
    val selectedCategory: GarmentCategory? = null,
    val selectedWeathers: Set<WeatherCondition> = emptySet(),
    val selectedOccasions: Set<Occasion> = emptySet(),
    val nameError: String? = null,
    val categoryError: String? = null,
    val step: ClassifyStep = ClassifyStep.BASIC,
    val isProcessingImage: Boolean = false
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
    private val context: Context,
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassifyGarmentUiState(imageUri = imageUri))
    val uiState: StateFlow<ClassifyGarmentUiState> = _uiState.asStateFlow()

    init {
        removeBackground(imageUri)
    }

    fun onEvent(event: ClassifyGarmentEvent) {
        when (event) {
            is ClassifyGarmentEvent.NameChanged       -> _uiState.update { it.copy(name = event.value, nameError = null) }
            is ClassifyGarmentEvent.SelectCategory    -> _uiState.update { it.copy(selectedCategory = event.category, categoryError = null) }
            is ClassifyGarmentEvent.ToggleWeather     -> toggleWeather(event.weather)
            is ClassifyGarmentEvent.ToggleOccasion    -> toggleOccasion(event.occasion)
            is ClassifyGarmentEvent.Continue          -> validateAndAdvance()
            is ClassifyGarmentEvent.Save              -> save()
            is ClassifyGarmentEvent.Back              -> goBack()
        }
    }

    private fun removeBackground(imageUri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true) }
            try {
                val file = withContext(Dispatchers.IO) { uriToFile(imageUri) }
                    ?: run {
                        _uiState.update { it.copy(isProcessingImage = false) }
                        return@launch
                    }

                RemoveBgService.removeBackground(file)
                    .onSuccess { pngBytes ->
                        val outputFile = withContext(Dispatchers.IO) {
                            val out = File(context.cacheDir, "removebg_${System.currentTimeMillis()}.png")
                            out.writeBytes(pngBytes)
                            out
                        }
                        _uiState.update {
                            it.copy(
                                imageUri = Uri.fromFile(outputFile).toString(),
                                isProcessingImage = false
                            )
                        }
                    }
                    .onFailure {
                        // Si falla usa la imagen original sin bloquear al usuario
                        _uiState.update { it.copy(isProcessingImage = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingImage = false) }
            }
        }
    }

    private fun uriToFile(uriString: String): File? {
        return try {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: return null)
                return if (file.exists()) file else null
            }
            
            // Para content:// URIs (Galeria/Camara), copiamos a un archivo temporal
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.png")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            android.util.Log.e("ClassifyGarment", "Error converting URI to file", e)
            null
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
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingImage = true) }

            runCatching {
                val imageUrl = withContext(Dispatchers.IO) {
                    uriToFile(state.imageUri)?.let { CloudinaryService.upload(it) }
                } ?: state.imageUri

                garmentRepository.createGarment(
                    ownerUserId = userRepository.currentUserId,
                    name = state.name.trim(),
                    category = requireNotNull(state.selectedCategory),
                    imageUrl = imageUrl,
                    suitableWeather = state.selectedWeathers,
                    suitableOccasions = state.selectedOccasions
                )
            }.onSuccess { garment ->
                analyticsTracker.track(
                    AnalyticsEvents.garmentSaved(
                        category = garment.category.name,
                        weatherCount = garment.suitableWeather.size,
                        occasionCount = garment.suitableOccasions.size
                    )
                )
                _uiState.update { it.copy(step = ClassifyStep.SAVED, isProcessingImage = false) }
            }.onFailure { error ->
                crashReporter.recordException(
                    throwable = error,
                    keys = mapOf(
                        "feature" to "garments",
                        "operation" to "create",
                        "category" to state.selectedCategory?.name
                    )
                )
                _uiState.update { it.copy(isProcessingImage = false) }
            }
        }
    }
}
