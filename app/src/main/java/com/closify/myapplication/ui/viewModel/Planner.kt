package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.data.repository.WeatherRepository
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val PlannerDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

enum class PlannerStep {
    DATE_SELECTION,
    OUTFIT_SELECTION,
    PLANNING_REVIEW
}

data class PlannerUiState(
    val step: PlannerStep = PlannerStep.DATE_SELECTION,
    val selectedDate: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.from(selectedDate),
    val dateInput: String = selectedDate.format(PlannerDateFormatter),
    val forecastDays: List<PlannerForecastDay> = emptyList(),
    val topAndOuterwearGarments: List<Garment> = emptyList(),
    val bottomGarments: List<Garment> = emptyList(),
    val footwearGarments: List<Garment> = emptyList(),
    val fullBodyGarments: List<Garment> = emptyList(),
    val useFullBody: Boolean = false,
    val selectedTopAndOuterwearGarmentId: String? = null,
    val selectedBottomGarmentId: String? = null,
    val selectedFootwearGarmentId: String? = null,
    val selectedFullBodyGarmentId: String? = null,
    val plannedGarments: List<Garment> = emptyList(),
    val plannedOutfitTitle: String = "",
    val plannedPosts: List<OutfitPost> = emptyList(),
    val selectedPlannedPost: OutfitPost? = null,
    val editingPostId: String? = null,
    val showSavedDialog: Boolean = false,
    val showNoFullBodyDialog: Boolean = false
) {
    val selectedForecast: PlannerForecastDay?
        get() = forecastDays.firstOrNull { it.date == selectedDate && it.weather != WeatherCondition.ANY }

    val currentCarouselGarments: List<Garment>
        get() {
            val selectedIds = if (useFullBody) {
                listOfNotNull(selectedFullBodyGarmentId, selectedFootwearGarmentId)
            } else {
                listOfNotNull(
                    selectedTopAndOuterwearGarmentId,
                    selectedBottomGarmentId,
                    selectedFootwearGarmentId
                )
            }

            val allGarments = topAndOuterwearGarments + bottomGarments + footwearGarments + fullBodyGarments
            return selectedIds.mapNotNull { selectedId -> allGarments.firstOrNull { it.id == selectedId } }
        }
}

class PlannerViewModel(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val outfitRepository: OutfitRepository = OutfitRepository.instance,
    private val weatherRepository: WeatherRepository = WeatherRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        initialLoad()
    }

    private fun initialLoad() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserOrDefault().id
            val plannedPosts = outfitRepository.getPlannedPosts(userId)
            val forecast = weatherRepository.getPlannerForecast(today)
            val garmentGroups = garmentRepository.getPlannerGroups(userId)

            _uiState.update { currentState -> 
                currentState.copy(
                    selectedDate = today,
                    visibleMonth = YearMonth.from(today),
                    dateInput = today.format    (PlannerDateFormatter),
                    forecastDays = forecast,
                    plannedPosts = plannedPosts,
                    topAndOuterwearGarments = garmentGroups.topAndOuterwear,
                    bottomGarments = garmentGroups.bottoms,
                    footwearGarments = garmentGroups.footwear,
                    fullBodyGarments = garmentGroups.fullBody,
                    selectedTopAndOuterwearGarmentId = defaultSelectedId(garmentGroups.topAndOuterwear),
                    selectedBottomGarmentId = defaultSelectedId(garmentGroups.bottoms),
                    selectedFootwearGarmentId = defaultSelectedId(garmentGroups.footwear),
                    selectedFullBodyGarmentId = defaultSelectedId(garmentGroups.fullBody)
                ) 
            }
        }
    }

    fun onDateInputChange(value: String) {
        val sanitizedValue = value.toDateInputFormat()
        val parsedDate = parseDateOrNull(sanitizedValue)

        _uiState.update { currentState ->
            if (parsedDate != null && !parsedDate.isBefore(today)) {
                currentState.copy(
                    selectedDate = parsedDate,
                    visibleMonth = YearMonth.from(parsedDate),
                    dateInput = sanitizedValue
                )
            } else {
                currentState.copy(dateInput = sanitizedValue)
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        if (date.isBefore(today)) return

        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserOrDefault().id
            val plannedPost = outfitRepository.getPlannedPostByDate(currentUserId, date.toSpanishTitle())
            _uiState.update { currentState -> 
                currentState.copy(
                    selectedDate = date,
                    visibleMonth = YearMonth.from(date),
                    dateInput = date.format(PlannerDateFormatter),
                    selectedPlannedPost = plannedPost
                ) 
            }
        }
    }

    fun onCancelSelection() {
        onDateSelected(today)
    }

    fun onConfirmSelection() {
        val parsedDate = parseDateOrNull(_uiState.value.dateInput)
        if (parsedDate == null || parsedDate.isBefore(today)) return

        _uiState.update { currentState -> 
            currentState.copy(
                selectedDate = parsedDate,
                visibleMonth = YearMonth.from(parsedDate),
                step = PlannerStep.OUTFIT_SELECTION
            ) 
        }
    }

    fun onBackToDateSelection() {
        _uiState.update { currentState -> currentState.copy(step = PlannerStep.DATE_SELECTION) }
    }

    fun onToggleFullBody(enabled: Boolean) {
        if (enabled && _uiState.value.fullBodyGarments.isEmpty()) {
            _uiState.update { currentState -> currentState.copy(showNoFullBodyDialog = true) }
            return
        }
        _uiState.update { currentState -> currentState.copy(useFullBody = enabled) }
    }

    fun onDismissNoFullBodyDialog() {
        _uiState.update { currentState -> currentState.copy(showNoFullBodyDialog = false) }
    }

    fun onTopAndOuterwearCentered(garmentId: String) {
        _uiState.update { currentState -> currentState.copy(selectedTopAndOuterwearGarmentId = garmentId) }
    }

    fun onBottomCentered(garmentId: String) {
        _uiState.update { currentState -> currentState.copy(selectedBottomGarmentId = garmentId) }
    }

    fun onFootwearCentered(garmentId: String) {
        _uiState.update { currentState -> currentState.copy(selectedFootwearGarmentId = garmentId) }
    }

    fun onFullBodyCentered(garmentId: String) {
        _uiState.update { currentState -> currentState.copy(selectedFullBodyGarmentId = garmentId) }
    }

    fun onContinueToPlanningReview() {
        _uiState.update { state ->
            state.copy(
                plannedGarments = (state.plannedGarments + state.currentCarouselGarments).distinctBy { it.id },
                step = PlannerStep.PLANNING_REVIEW
            )
        }
    }

    fun onAddMoreGarments() {
        _uiState.update { currentState -> 
            currentState.withPlannedGarmentsAsCarouselSelection().copy(step = PlannerStep.OUTFIT_SELECTION) 
        }
    }

    fun onBackFromPlanningReview() {
        _uiState.update { currentState -> currentState.copy(step = PlannerStep.OUTFIT_SELECTION) }
    }

    fun onPlannedOutfitTitleChange(value: String) {
        _uiState.update { currentState -> currentState.copy(plannedOutfitTitle = value.take(100)) }
    }

    fun onPlannedGarmentsChange(garments: List<Garment>) {
        _uiState.update { currentState -> currentState.copy(plannedGarments = garments.distinctBy { it.id }) }
    }

    fun onEditSelectedPlannedPost() {
        val post = _uiState.value.selectedPlannedPost ?: return
        _uiState.update { currentState -> 
            currentState.copy(
                selectedPlannedPost = null,
                editingPostId = post.id,
                plannedGarments = post.outfit.garments,
                plannedOutfitTitle = post.title.orEmpty(),
                step = PlannerStep.PLANNING_REVIEW
            ).withPlannedGarmentsAsCarouselSelection() 
        }
    }

    fun onDeleteSelectedPlannedPost() {
        viewModelScope.launch {
            val post = _uiState.value.selectedPlannedPost ?: return@launch
            val userId = userRepository.getCurrentUserOrDefault().id
            outfitRepository.deletePlannedOutfitPost(post.id)
            val updatedPosts = outfitRepository.getPlannedPosts(userId)
            _uiState.update { currentState -> 
                currentState.copy(
                    plannedPosts = updatedPosts,
                    selectedPlannedPost = null
                ) 
            }
        }
    }

    fun onDismissSelectedPlannedPost() {
        _uiState.update { currentState -> currentState.copy(selectedPlannedPost = null) }
    }

    fun onSavePlanning() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.plannedGarments.isEmpty()) return@launch

            val userId = userRepository.getCurrentUserOrDefault().id
            val plannedDate = state.selectedDate.toSpanishTitle()
            outfitRepository.savePlanning(
                userId = userId,
                title = state.plannedOutfitTitle,
                garments = state.plannedGarments,
                plannedDate = plannedDate,
                createdAt = today.toSpanishTitle(),
                editingPostId = state.editingPostId
            )

            val updatedPosts = outfitRepository.getPlannedPosts(userId)
            _uiState.update { currentState -> 
                currentState.copy(
                    plannedPosts = updatedPosts,
                    editingPostId = null,
                    showSavedDialog = true
                ) 
            }
        }
    }

    fun onSavedDialogContinue() {
        _uiState.update { currentState -> 
            currentState.copy(
                showSavedDialog = false,
                step = PlannerStep.DATE_SELECTION,
                plannedGarments = emptyList(),
                plannedOutfitTitle = ""
            ) 
        }
    }

    private fun defaultSelectedId(garments: List<Garment>): String? =
        (garments.getOrNull(1) ?: garments.firstOrNull())?.id

    private fun PlannerUiState.withPlannedGarmentsAsCarouselSelection(): PlannerUiState {
        val plannedIds = plannedGarments.map { it.id }.toSet()
        return copy(
            selectedTopAndOuterwearGarmentId = topAndOuterwearGarments
                .firstOrNull { it.id in plannedIds }
                ?.id
                ?: selectedTopAndOuterwearGarmentId,
            selectedBottomGarmentId = bottomGarments
                .firstOrNull { it.id in plannedIds }
                ?.id
                ?: selectedBottomGarmentId,
            selectedFootwearGarmentId = footwearGarments
                .firstOrNull { it.id in plannedIds }
                ?.id
                ?: selectedFootwearGarmentId,
            selectedFullBodyGarmentId = fullBodyGarments
                .firstOrNull { it.id in plannedIds }
                ?.id
                ?: selectedFullBodyGarmentId
        )
    }

    private fun parseDateOrNull(value: String): LocalDate? =
        try {
            LocalDate.parse(value, PlannerDateFormatter)
        } catch (_: DateTimeParseException) {
            null
        }

    private fun String.toDateInputFormat(): String {
        val digits = filter(Char::isDigit).take(8)
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append("/")
                append(char)
            }
        }
    }

    private fun LocalDate.toSpanishTitle(): String {
        val month = when (monthValue) {
            1 -> "enero"
            2 -> "febrero"
            3 -> "marzo"
            4 -> "abril"
            5 -> "mayo"
            6 -> "junio"
            7 -> "julio"
            8 -> "agosto"
            9 -> "septiembre"
            10 -> "octubre"
            11 -> "noviembre"
            else -> "diciembre"
        }

        return "$dayOfMonth de $month de $year"
    }
}
