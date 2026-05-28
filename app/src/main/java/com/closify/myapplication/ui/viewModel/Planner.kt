package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

data class PlannerForecastDay(
    val date: LocalDate,
    val weather: WeatherCondition,
    val temperature: Int,
    val label: String
)

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
    val showSavedDialog: Boolean = false
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
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    fun onDateInputChange(value: String) {
        val sanitizedValue = value.toDateInputFormat()
        val parsedDate = parseDateOrNull(sanitizedValue)

        _uiState.value = if (parsedDate != null && !parsedDate.isBefore(today)) {
            _uiState.value.copy(
                selectedDate = parsedDate,
                visibleMonth = YearMonth.from(parsedDate),
                dateInput = sanitizedValue
            )
        } else {
            _uiState.value.copy(dateInput = sanitizedValue)
        }
    }

    fun onDateSelected(date: LocalDate) {
        if (date.isBefore(today)) return

        val plannedPost = _uiState.value.plannedPosts.firstOrNull { it.plannedDate == date.toSpanishTitle() }
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            visibleMonth = YearMonth.from(date),
            dateInput = date.format(PlannerDateFormatter),
            selectedPlannedPost = plannedPost
        )
    }

    fun onCancelSelection() {
        onDateSelected(today)
    }

    fun onConfirmSelection() {
        val parsedDate = parseDateOrNull(_uiState.value.dateInput)
        if (parsedDate == null || parsedDate.isBefore(today)) return

        _uiState.value = _uiState.value.copy(
            selectedDate = parsedDate,
            visibleMonth = YearMonth.from(parsedDate),
            step = PlannerStep.OUTFIT_SELECTION
        )
    }

    fun onBackToDateSelection() {
        _uiState.value = _uiState.value.copy(step = PlannerStep.DATE_SELECTION)
    }

    fun onToggleFullBody(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(useFullBody = enabled)
    }

    fun onTopAndOuterwearCentered(garmentId: String) {
        _uiState.value = _uiState.value.copy(selectedTopAndOuterwearGarmentId = garmentId)
    }

    fun onBottomCentered(garmentId: String) {
        _uiState.value = _uiState.value.copy(selectedBottomGarmentId = garmentId)
    }

    fun onFootwearCentered(garmentId: String) {
        _uiState.value = _uiState.value.copy(selectedFootwearGarmentId = garmentId)
    }

    fun onFullBodyCentered(garmentId: String) {
        _uiState.value = _uiState.value.copy(selectedFullBodyGarmentId = garmentId)
    }

    fun onContinueToPlanningReview() {
        val state = _uiState.value
        _uiState.value = state.copy(
            plannedGarments = (state.plannedGarments + state.currentCarouselGarments).distinctBy { it.id },
            step = PlannerStep.PLANNING_REVIEW
        )
    }

    fun onAddMoreGarments() {
        _uiState.value = _uiState.value.withPlannedGarmentsAsCarouselSelection()
            .copy(step = PlannerStep.OUTFIT_SELECTION)
    }

    fun onBackFromPlanningReview() {
        _uiState.value = _uiState.value.copy(step = PlannerStep.OUTFIT_SELECTION)
    }

    fun onPlannedOutfitTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(plannedOutfitTitle = value.take(100))
    }

    fun onPlannedGarmentsChange(garments: List<Garment>) {
        _uiState.value = _uiState.value.copy(plannedGarments = garments.distinctBy { it.id })
    }

    fun onEditSelectedPlannedPost() {
        val post = _uiState.value.selectedPlannedPost ?: return
        _uiState.value = _uiState.value.copy(
            selectedPlannedPost = null,
            editingPostId = post.id,
            plannedGarments = post.outfit.garments,
            plannedOutfitTitle = post.title.orEmpty(),
            step = PlannerStep.PLANNING_REVIEW
        ).withPlannedGarmentsAsCarouselSelection()
    }

    fun onDeleteSelectedPlannedPost() {
        val post = _uiState.value.selectedPlannedPost ?: return
        outfitRepository.deletePlannedOutfitPost(post.id)
        _uiState.value = _uiState.value.copy(
            plannedPosts = outfitRepository.getPlannedPosts(userRepository.getCurrentUserOrDefault().id),
            selectedPlannedPost = null
        )
    }

    fun onDismissSelectedPlannedPost() {
        _uiState.value = _uiState.value.copy(selectedPlannedPost = null)
    }

    fun onSavePlanning() {
        val state = _uiState.value
        if (state.plannedGarments.isEmpty()) return

        val userId = userRepository.getCurrentUserOrDefault().id
        val outfit = Outfit(
            id = state.editingPostId?.let { "outfit_$it" } ?: "planned_outfit_${System.currentTimeMillis()}",
            garments = state.plannedGarments,
            ownerUserId = userId,
            name = state.plannedOutfitTitle.ifBlank { null },
            createdAt = today.toSpanishTitle()
        )
        val plannedDate = state.selectedDate.toSpanishTitle()

        if (state.editingPostId == null) {
            outfitRepository.savePlannedOutfitPost(
                userId = userId,
                title = state.plannedOutfitTitle,
                outfit = outfit,
                plannedDate = plannedDate,
                createdAt = today.toSpanishTitle()
            )
        } else {
            outfitRepository.updatePlannedOutfitPost(
                postId = state.editingPostId,
                title = state.plannedOutfitTitle,
                outfit = outfit,
                plannedDate = plannedDate
            )
        }

        _uiState.value = state.copy(
            plannedPosts = outfitRepository.getPlannedPosts(userId),
            editingPostId = null,
            showSavedDialog = true
        )
    }

    fun onSavedDialogContinue() {
        _uiState.value = _uiState.value.copy(
            showSavedDialog = false,
            step = PlannerStep.DATE_SELECTION,
            plannedGarments = emptyList(),
            plannedOutfitTitle = ""
        )
    }

    private fun buildInitialState(): PlannerUiState {
        val userId = userRepository.getCurrentUserOrDefault().id
        val garments = garmentRepository.getAllByUserId(userId)
        val plannedPosts = outfitRepository.getPlannedPosts(userId)

        val topAndOuterwear = garments.filter {
            it.category == GarmentCategory.TOP || it.category == GarmentCategory.OUTERWEAR
        }
        val bottoms = garments.filter { it.category == GarmentCategory.BOTTOM }
        val footwear = garments.filter { it.category == GarmentCategory.FOOTWEAR }
        val fullBody = garments.filter { it.category == GarmentCategory.FULL_BODY }

        return PlannerUiState(
            selectedDate = today,
            visibleMonth = YearMonth.from(today),
            dateInput = today.format(PlannerDateFormatter),
            forecastDays = buildMockForecast(today),
            topAndOuterwearGarments = topAndOuterwear,
            bottomGarments = bottoms,
            footwearGarments = footwear,
            fullBodyGarments = fullBody,
            selectedTopAndOuterwearGarmentId = defaultSelectedId(topAndOuterwear),
            selectedBottomGarmentId = defaultSelectedId(bottoms),
            selectedFootwearGarmentId = defaultSelectedId(footwear),
            selectedFullBodyGarmentId = defaultSelectedId(fullBody),
            plannedPosts = plannedPosts
        )
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

    private fun buildMockForecast(startDate: LocalDate): List<PlannerForecastDay> {
        val weatherByOffset = listOf(
            WeatherCondition.HOT to (32 to "Caluroso"),
            WeatherCondition.HOT to (30 to "Caluroso"),
            WeatherCondition.HOT to (29 to "Caluroso"),
            WeatherCondition.MILD to (20 to "Templado"),
            WeatherCondition.MILD to (21 to "Templado"),
            WeatherCondition.COLD to (12 to "Lluvioso"),
            WeatherCondition.WINDY to (16 to "Ventoso")
        )

        return weatherByOffset.mapIndexed { index, forecast ->
            PlannerForecastDay(
                date = startDate.plusDays(index.toLong()),
                weather = forecast.first,
                temperature = forecast.second.first,
                label = forecast.second.second
            )
        }
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
