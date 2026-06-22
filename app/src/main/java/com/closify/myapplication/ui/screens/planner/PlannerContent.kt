package com.closify.myapplication.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyConfirmationDialog
import com.closify.myapplication.ui.screens.planner.components.EditPlannedGarmentsDialog
import com.closify.myapplication.ui.screens.planner.components.OutfitCarouselArea
import com.closify.myapplication.ui.screens.planner.components.PlannerCalendarCard
import com.closify.myapplication.ui.screens.planner.components.PlannerDateField
import com.closify.myapplication.ui.screens.planner.components.PlannerDateSummaryCard
import com.closify.myapplication.ui.screens.planner.components.PlannerTitleField
import com.closify.myapplication.ui.screens.planner.components.PlannerTopBar
import com.closify.myapplication.ui.screens.planner.components.PlannedGarmentsCard
import com.closify.myapplication.ui.screens.planner.components.PlannedOutfitDialog
import com.closify.myapplication.ui.screens.planner.components.WeatherInfoRow
import com.closify.myapplication.ui.screens.planner.components.toSpanishTitle
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.domain.model.PlannerForecastDay
import com.closify.myapplication.ui.viewmodel.PlannerStep
import com.closify.myapplication.ui.viewmodel.PlannerUiState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val InputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun PlannerContent(
    uiState: PlannerUiState,
    onDateInputChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onCancelSelection: () -> Unit,
    onConfirmSelection: () -> Unit,
    onBackToDateSelection: () -> Unit,
    onToggleFullBody: (Boolean) -> Unit,
    onTopAndOuterwearCentered: (String) -> Unit,
    onBottomCentered: (String) -> Unit,
    onFootwearCentered: (String) -> Unit,
    onFullBodyCentered: (String) -> Unit,
    onContinueToPlanningReview: () -> Unit,
    onBackFromPlanningReview: () -> Unit,
    onAddMoreGarments: () -> Unit,
    onPlannedOutfitTitleChange: (String) -> Unit,
    onPlannedGarmentsChange: (List<Garment>) -> Unit,
    onSavePlanning: () -> Unit,
    onSavedDialogContinue: () -> Unit,
    onEditSelectedPlannedPost: () -> Unit,
    onDeleteSelectedPlannedPost: () -> Unit,
    onDismissSelectedPlannedPost: () -> Unit,
    modifier: Modifier = Modifier
) {
    uiState.selectedPlannedPost?.let { plannedPost ->
        PlannedOutfitDialog(
            post = plannedPost,
            forecast = uiState.selectedForecast,
            onEditClick = onEditSelectedPlannedPost,
            onDeleteClick = onDeleteSelectedPlannedPost,
            onDismiss = onDismissSelectedPlannedPost
        )
    }

    if (uiState.showSavedDialog) {
        ClosifyConfirmationDialog(
            title = "¡Outfit guardado!",
            subtitle = "Podés verlo en tu calendario y en tus\npost de outfits planificados",
            onDismiss = onSavedDialogContinue
        )
    }

    when (uiState.step) {
        PlannerStep.DATE_SELECTION -> PlannerDateSelectionContent(
            uiState = uiState,
            onDateInputChange = onDateInputChange,
            onDateSelected = onDateSelected,
            onCancelSelection = onCancelSelection,
            onConfirmSelection = onConfirmSelection,
            modifier = modifier
        )
        PlannerStep.OUTFIT_SELECTION -> PlannerOutfitSelectionContent(
            uiState = uiState,
            onBackToDateSelection = onBackToDateSelection,
            onToggleFullBody = onToggleFullBody,
            onTopAndOuterwearCentered = onTopAndOuterwearCentered,
            onBottomCentered = onBottomCentered,
            onFootwearCentered = onFootwearCentered,
            onFullBodyCentered = onFullBodyCentered,
            onContinueToPlanningReview = onContinueToPlanningReview,
            modifier = modifier
        )
        PlannerStep.PLANNING_REVIEW -> PlannerReviewContent(
            uiState = uiState,
            onBack = onBackFromPlanningReview,
            onAddMoreGarments = onAddMoreGarments,
            onPlannedOutfitTitleChange = onPlannedOutfitTitleChange,
            onPlannedGarmentsChange = onPlannedGarmentsChange,
            onSavePlanning = onSavePlanning,
            modifier = modifier
        )
    }
}

@Composable
private fun PlannerDateSelectionContent(
    uiState: PlannerUiState,
    onDateInputChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onCancelSelection: () -> Unit,
    onConfirmSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        item { PlannerTopBar() }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(42.dp))
                Text(
                    text = "Planificador",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Selecciona una fecha para ver el pronóstico\ny arma el outfit perfecto antes de salir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(30.dp))
                PlannerDateField(value = uiState.dateInput, onValueChange = onDateInputChange, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DD/MM/YYYY",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                PlannerCalendarCard(
                    uiState = uiState,
                    onDateSelected = onDateSelected,
                    onCancelSelection = onCancelSelection,
                    onConfirmSelection = onConfirmSelection
                )
            }
        }
    }
}

@Composable
private fun PlannerOutfitSelectionContent(
    uiState: PlannerUiState,
    onBackToDateSelection: () -> Unit,
    onToggleFullBody: (Boolean) -> Unit,
    onTopAndOuterwearCentered: (String) -> Unit,
    onBottomCentered: (String) -> Unit,
    onFootwearCentered: (String) -> Unit,
    onFullBodyCentered: (String) -> Unit,
    onContinueToPlanningReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        PlannerTopBar(onBackClick = onBackToDateSelection)
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.selectedDate.toSpanishTitle(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            val forecast = uiState.selectedForecast
            if (forecast == null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Aún no tenemos información del clima para\nel día seleccionado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                WeatherInfoRow(forecast = forecast)
            }
        }
        OutfitCarouselArea(
            uiState = uiState,
            onTopAndOuterwearCentered = onTopAndOuterwearCentered,
            onBottomCentered = onBottomCentered,
            onFootwearCentered = onFootwearCentered,
            onFullBodyCentered = onFullBodyCentered,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = uiState.useFullBody,
                    onCheckedChange = onToggleFullBody,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "FullBody",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinueToPlanningReview,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.width(156.dp).height(40.dp)
            ) {
                Text(text = "Continuar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PlannerReviewContent(
    uiState: PlannerUiState,
    onBack: () -> Unit,
    onAddMoreGarments: () -> Unit,
    onPlannedOutfitTitleChange: (String) -> Unit,
    onPlannedGarmentsChange: (List<Garment>) -> Unit,
    onSavePlanning: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditGarmentsDialog by remember { mutableStateOf(false) }

    if (showEditGarmentsDialog) {
        EditPlannedGarmentsDialog(
            garments = uiState.plannedGarments,
            onSaveChanges = { updated ->
                onPlannedGarmentsChange(updated)
                showEditGarmentsDialog = false
            },
            onDismiss = { showEditGarmentsDialog = false }
        )
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        PlannerTopBar(onBackClick = onBack)
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Editar planificación",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Tu outfit para este día", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            PlannedGarmentsCard(garments = uiState.plannedGarments, onEditClick = { showEditGarmentsDialog = true })
            Spacer(modifier = Modifier.height(22.dp))
            PlannerTitleField(value = uiState.plannedOutfitTitle, onValueChange = onPlannedOutfitTitleChange)
            Spacer(modifier = Modifier.height(18.dp))
            PlannerDateSummaryCard(uiState = uiState)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAddMoreGarments,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.weight(1f).height(40.dp)
            ) {
                Text(text = "Añadir mas prendas", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1)
            }
            Button(
                onClick = onSavePlanning,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.weight(1f).height(40.dp)
            ) {
                Text(text = "Guardar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PlannerDateSelectionPreview() {
    val selectedDate = LocalDate.of(2026, 8, 17)
    ClosifyTheme {
        PlannerContent(
            uiState = previewState(selectedDate),
            onDateInputChange = {}, onDateSelected = {}, onCancelSelection = {}, onConfirmSelection = {},
            onBackToDateSelection = {}, onToggleFullBody = {}, onTopAndOuterwearCentered = {},
            onBottomCentered = {}, onFootwearCentered = {}, onFullBodyCentered = {},
            onContinueToPlanningReview = {}, onBackFromPlanningReview = {}, onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {}, onPlannedGarmentsChange = {}, onSavePlanning = {},
            onSavedDialogContinue = {}, onEditSelectedPlannedPost = {}, onDeleteSelectedPlannedPost = {},
            onDismissSelectedPlannedPost = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PlannerOutfitSelectionPreview() {
    val selectedDate = LocalDate.of(2026, 8, 17)
    ClosifyTheme {
        PlannerContent(
            uiState = previewState(selectedDate).copy(step = PlannerStep.OUTFIT_SELECTION),
            onDateInputChange = {}, onDateSelected = {}, onCancelSelection = {}, onConfirmSelection = {},
            onBackToDateSelection = {}, onToggleFullBody = {}, onTopAndOuterwearCentered = {},
            onBottomCentered = {}, onFootwearCentered = {}, onFullBodyCentered = {},
            onContinueToPlanningReview = {}, onBackFromPlanningReview = {}, onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {}, onPlannedGarmentsChange = {}, onSavePlanning = {},
            onSavedDialogContinue = {}, onEditSelectedPlannedPost = {}, onDeleteSelectedPlannedPost = {},
            onDismissSelectedPlannedPost = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PlannerReviewPreview() {
    val selectedDate = LocalDate.of(2026, 8, 17)
    ClosifyTheme {
        PlannerContent(
            uiState = previewState(selectedDate).let { it.copy(step = PlannerStep.PLANNING_REVIEW, plannedGarments = it.currentCarouselGarments) },
            onDateInputChange = {}, onDateSelected = {}, onCancelSelection = {}, onConfirmSelection = {},
            onBackToDateSelection = {}, onToggleFullBody = {}, onTopAndOuterwearCentered = {},
            onBottomCentered = {}, onFootwearCentered = {}, onFullBodyCentered = {},
            onContinueToPlanningReview = {}, onBackFromPlanningReview = {}, onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {}, onPlannedGarmentsChange = {}, onSavePlanning = {},
            onSavedDialogContinue = {}, onEditSelectedPlannedPost = {}, onDeleteSelectedPlannedPost = {},
            onDismissSelectedPlannedPost = {}
        )
    }
}

private fun previewState(selectedDate: LocalDate): PlannerUiState {
    val garments = listOf(
        previewGarment("blusa_1", "Blusa rosa", GarmentCategory.TOP),
        previewGarment("blusa_elegante_1", "Blusa elegante", GarmentCategory.TOP),
        previewGarment("campera_jean", "Campera de jean", GarmentCategory.OUTERWEAR),
        previewGarment("falda_elegante", "Falda elegante", GarmentCategory.BOTTOM),
        previewGarment("pantalon_beige", "Pantalon beige", GarmentCategory.BOTTOM),
        previewGarment("jean_1", "Jean claro", GarmentCategory.BOTTOM),
        previewGarment("zapatos_elegantes_1", "Zapatos elegantes", GarmentCategory.FOOTWEAR),
        previewGarment("zapatillas_negras", "Zapatillas negras", GarmentCategory.FOOTWEAR),
        previewGarment("zapatillas_blancas", "Zapatillas blancas", GarmentCategory.FOOTWEAR),
        previewGarment("vestido_floral", "Vestido floral", GarmentCategory.FULL_BODY)
    )
    val topAndOuterwear = garments.filter { it.category == GarmentCategory.TOP || it.category == GarmentCategory.OUTERWEAR }
    val bottoms   = garments.filter { it.category == GarmentCategory.BOTTOM }
    val footwear  = garments.filter { it.category == GarmentCategory.FOOTWEAR }
    val fullBody  = garments.filter { it.category == GarmentCategory.FULL_BODY }

    return PlannerUiState(
        selectedDate = selectedDate,
        visibleMonth = YearMonth.from(selectedDate),
        dateInput = selectedDate.format(InputDateFormatter),
        forecastDays = listOf(PlannerForecastDay(selectedDate, WeatherCondition.MILD, 20, "Templado")),
        topAndOuterwearGarments = topAndOuterwear,
        bottomGarments = bottoms,
        footwearGarments = footwear,
        fullBodyGarments = fullBody,
        selectedTopAndOuterwearGarmentId = topAndOuterwear.firstOrNull()?.id,
        selectedBottomGarmentId = bottoms.firstOrNull()?.id,
        selectedFootwearGarmentId = footwear.firstOrNull()?.id,
        selectedFullBodyGarmentId = fullBody.firstOrNull()?.id
    )
}

private fun previewGarment(imageName: String, name: String, category: GarmentCategory): Garment = Garment(
    id = imageName,
    ownerUserId = "preview",
    name = name,
    category = category,
    imageUrl = "android.resource://com.closify.myapplication/drawable/$imageName",
    suitableWeather = setOf(WeatherCondition.ANY),
    suitableOccasions = setOf(Occasion.ANY)
)
