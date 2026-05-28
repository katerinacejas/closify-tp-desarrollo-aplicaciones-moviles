package com.closify.myapplication.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.components.ClosifyConfirmationDialog
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.theme.LavandaAccent
import com.closify.myapplication.ui.theme.PrimaryDark
import com.closify.myapplication.ui.theme.RosaSecondary
import com.closify.myapplication.ui.viewmodel.PlannerForecastDay
import com.closify.myapplication.ui.viewmodel.PlannerStep
import com.closify.myapplication.ui.viewmodel.PlannerUiState
import kotlinx.coroutines.flow.distinctUntilChanged
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
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        item {
            PlannerTopBar()
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
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

                PlannerDateField(
                    value = uiState.dateInput,
                    onValueChange = onDateInputChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "DD/MM/YYYY",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PlannerTopBar(onBackClick = onBackToDateSelection)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 8.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = uiState.useFullBody,
                    onCheckedChange = onToggleFullBody,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = PrimaryDark,
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .width(156.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.labelLarge
                )
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
            onSaveChanges = { updatedGarments ->
                onPlannedGarmentsChange(updatedGarments)
                showEditGarmentsDialog = false
            },
            onDismiss = { showEditGarmentsDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PlannerTopBar(onBackClick = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Editar planificación",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tu outfit para este día",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            PlannedGarmentsCard(
                garments = uiState.plannedGarments,
                onEditClick = { showEditGarmentsDialog = true }
            )

            Spacer(modifier = Modifier.height(22.dp))

            PlannerTitleField(
                value = uiState.plannedOutfitTitle,
                onValueChange = onPlannedOutfitTitleChange
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlannerDateSummaryCard(uiState = uiState)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAddMoreGarments,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RosaSecondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Añadir mas prendas",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }

            Button(
                onClick = onSavePlanning,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDark,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Guardar",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun PlannedGarmentsCard(
    garments: List<Garment>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(154.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, RosaSecondary, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 34.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            garments.take(4).forEach { garment ->
                PlannerGarmentImage(
                    garment = garment,
                    alpha = 1f,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .height(122.dp)
                )
            }
        }

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Editar prendas",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PlannerTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Titulo del outfit planificado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        supportingText = {
            Text(
                text = "${value.length}/100",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = PrimaryDark
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(116.dp)
    )
}

@Composable
private fun PlannerDateSummaryCard(
    uiState: PlannerUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Se planificará para el día:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.selectedDate.toSpanishTitle(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val forecast = uiState.selectedForecast
            if (forecast == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Aún no tenemos información del clima para ese día\nseleccionado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
                WeatherInfoRow(forecast = forecast)
            }
        }
    }
}

@Composable
private fun EditPlannedGarmentsDialog(
    garments: List<Garment>,
    onSaveChanges: (List<Garment>) -> Unit,
    onDismiss: () -> Unit
) {
    var draftGarments by remember(garments) { mutableStateOf(garments) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(658.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Editar prendas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(draftGarments, key = { it.id }) { garment ->
                        EditableGarmentBox(
                            garment = garment,
                            onRemove = {
                                draftGarments = draftGarments.filterNot { it.id == garment.id }
                            }
                        )
                    }
                }

                Button(
                    onClick = { onSaveChanges(draftGarments) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDark,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(172.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Guardar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannedOutfitDialog(
    post: OutfitPost,
    forecast: PlannerForecastDay?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(504.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = post.plannedDate.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (forecast == null) {
                    Text(
                        text = "Aún no tenemos información del clima para\nel día seleccionado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    WeatherInfoRow(forecast = forecast)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Tu outfit planificado para este día",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(178.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, RosaSecondary, RoundedCornerShape(24.dp))
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Column {
                        Text(
                            text = "Titulo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = post.title.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = 22.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            post.outfit.garments.take(4).forEach { garment ->
                                PlannerGarmentImage(
                                    garment = garment,
                                    alpha = 1f,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .weight(1f)
                                .height(98.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar planificación",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDark,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier
                        .width(190.dp)
                        .height(44.dp)
                ) {
                    Text(
                        text = "Eliminar planificación",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableGarmentBox(
    garment: Garment,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(152.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, RosaSecondary, RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        PlannerGarmentImage(
            garment = garment,
            alpha = 1f,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Quitar prenda",
                tint = PrimaryDark,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PlannerTopBar(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(start = if (onBackClick == null) 18.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            ClosifyLogo(size = 60.dp)
        }
    }
}

@Composable
private fun PlannerDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RosaSecondary,
            unfocusedBorderColor = RosaSecondary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = PrimaryDark,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.height(56.dp)
    )
}

@Composable
private fun PlannerCalendarCard(
    uiState: PlannerUiState,
    onDateSelected: (LocalDate) -> Unit,
    onCancelSelection: () -> Unit,
    onConfirmSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forecastByDate = uiState.forecastDays.associate { it.date to it.weather }
    val days = calendarDaysFor(uiState.visibleMonth)
    val today = LocalDate.now()
    val plannedDates = uiState.plannedPosts.mapNotNull { it.plannedDate }.toSet()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            CalendarWeekHeader()

            Spacer(modifier = Modifier.height(12.dp))

            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        CalendarDayCell(
                            date = date,
                            isCurrentMonth = YearMonth.from(date) == uiState.visibleMonth,
                            isSelected = date == uiState.selectedDate,
                            isEnabled = !date.isBefore(today),
                            hasPlannedOutfit = date.toSpanishTitle() in plannedDates,
                            weather = forecastByDate[date],
                            onDateSelected = onDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCancelSelection,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RosaSecondary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onConfirmSelection,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDark,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = "Confirmar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    val labels = listOf("D", "L", "M", "Mi", "J", "V", "S")

    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isEnabled: Boolean,
    hasPlannedOutfit: Boolean,
    weather: WeatherCondition?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isEnabled || !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .aspectRatio(0.92f)
            .clickable(enabled = isEnabled) { onDateSelected(date) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .then(
                    if (isSelected) {
                        Modifier
                            .clip(CircleShape)
                            .background(PrimaryDark)
                    } else if (hasPlannedOutfit) {
                        Modifier.border(2.dp, RosaSecondary, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }

        if (isEnabled) {
            weather?.let {
                WeatherIcon(
                    weather = it,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun WeatherInfoRow(
    forecast: PlannerForecastDay,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeatherIcon(weather = forecast.weather, modifier = Modifier.size(28.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${forecast.temperature}° ${forecast.label}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OutfitCarouselArea(
    uiState: PlannerUiState,
    onTopAndOuterwearCentered: (String) -> Unit,
    onBottomCentered: (String) -> Unit,
    onFootwearCentered: (String) -> Unit,
    onFullBodyCentered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (uiState.useFullBody) {
            SnapGarmentCarousel(
                garments = uiState.fullBodyGarments,
                itemWidth = 270.dp,
                itemHeight = 418.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.FillHeight,
                pageSpacing = 28.dp,
                selectedGarmentId = uiState.selectedFullBodyGarmentId,
                onCenteredGarmentChange = onFullBodyCentered,
                modifier = Modifier.height(430.dp)
            )

            SnapGarmentCarousel(
                garments = uiState.footwearGarments,
                itemWidth = 154.dp,
                itemHeight = 94.dp,
                sideItemAlpha = 0.48f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedFootwearGarmentId,
                onCenteredGarmentChange = onFootwearCentered,
                modifier = Modifier.height(110.dp)
            )
        } else {
            SnapGarmentCarousel(
                garments = uiState.topAndOuterwearGarments,
                itemWidth = 172.dp,
                itemHeight = 168.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedTopAndOuterwearGarmentId,
                onCenteredGarmentChange = onTopAndOuterwearCentered,
                modifier = Modifier.height(178.dp)
            )

            SnapGarmentCarousel(
                garments = uiState.bottomGarments,
                itemWidth = 172.dp,
                itemHeight = 178.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedBottomGarmentId,
                onCenteredGarmentChange = onBottomCentered,
                modifier = Modifier.height(188.dp)
            )

            SnapGarmentCarousel(
                garments = uiState.footwearGarments,
                itemWidth = 154.dp,
                itemHeight = 92.dp,
                sideItemAlpha = 0.48f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedFootwearGarmentId,
                onCenteredGarmentChange = onFootwearCentered,
                modifier = Modifier.height(104.dp)
            )
        }
    }
}

@Composable
private fun SnapGarmentCarousel(
    garments: List<Garment>,
    itemWidth: Dp,
    itemHeight: Dp,
    sideItemAlpha: Float,
    contentScale: ContentScale,
    pageSpacing: Dp,
    selectedGarmentId: String?,
    onCenteredGarmentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (garments.isEmpty()) return
    val selectedPage = selectedGarmentId
        ?.let { selectedId -> garments.indexOfFirst { it.id == selectedId } }
        ?.takeIf { it >= 0 }

    val pagerState = rememberPagerState(
        initialPage = selectedPage ?: if (garments.size > 1) 1 else 0,
        pageCount = { garments.size }
    )

    LaunchedEffect(pagerState, garments) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                garments.getOrNull(page)?.id?.let(onCenteredGarmentChange)
            }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sidePadding = if (maxWidth > itemWidth) {
            (maxWidth - itemWidth) / 2
        } else {
            0.dp
        }

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(itemWidth),
            contentPadding = PaddingValues(horizontal = sidePadding),
            pageSpacing = pageSpacing,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                val isSelected = pagerState.currentPage == page
                PlannerGarmentImage(
                    garment = garments[page],
                    alpha = if (isSelected) 1f else sideItemAlpha,
                    contentScale = contentScale,
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight)
                )
            }
        }
    }
}

@Composable
private fun PlannerGarmentImage(
    garment: Garment,
    alpha: Float,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(garment.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = garment.name,
        contentScale = contentScale,
        alpha = alpha,
        modifier = modifier
    )
}

@Composable
private fun WeatherIcon(
    weather: WeatherCondition,
    modifier: Modifier = Modifier
) {
    when (weather) {
        WeatherCondition.MILD -> MildWeatherIcon(modifier = modifier)
        WeatherCondition.ANY -> Unit
        else -> {
            val icon: ImageVector
            val tint: Color

            when (weather) {
                WeatherCondition.HOT -> {
                    icon = Icons.Rounded.WbSunny
                    tint = Color(0xFFFFD600)
                }
                WeatherCondition.COLD -> {
                    icon = Icons.Rounded.WaterDrop
                    tint = Color(0xFF1E88E5)
                }
                WeatherCondition.WINDY -> {
                    icon = Icons.Rounded.Cloud
                    tint = Color(0xFF1E88E5)
                }
                WeatherCondition.MILD -> {
                    icon = Icons.Rounded.WbSunny
                    tint = Color(0xFFFFD600)
                }
                WeatherCondition.ANY -> {
                    icon = Icons.Rounded.Cloud
                    tint = LavandaAccent
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun MildWeatherIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = null,
            tint = Color(0xFFFFD600),
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(15.dp)
        )
        Icon(
            imageVector = Icons.Rounded.Cloud,
            contentDescription = null,
            tint = Color(0xFF1E88E5),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(16.dp)
        )
    }
}

private fun calendarDaysFor(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val sundayBasedOffset = firstDay.dayOfWeek.value % 7
    val gridStart = firstDay.minusDays(sundayBasedOffset.toLong())

    return List(42) { index -> gridStart.plusDays(index.toLong()) }
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

@Preview(showSystemUi = true)
@Composable
private fun PlannerDateSelectionPreview() {
    val selectedDate = LocalDate.of(2026, 8, 17)

    ClosifyTheme {
        PlannerContent(
            uiState = previewState(selectedDate),
            onDateInputChange = {},
            onDateSelected = {},
            onCancelSelection = {},
            onConfirmSelection = {},
            onBackToDateSelection = {},
            onToggleFullBody = {},
            onTopAndOuterwearCentered = {},
            onBottomCentered = {},
            onFootwearCentered = {},
            onFullBodyCentered = {},
            onContinueToPlanningReview = {},
            onBackFromPlanningReview = {},
            onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {},
            onPlannedGarmentsChange = {},
            onSavePlanning = {},
            onSavedDialogContinue = {},
            onEditSelectedPlannedPost = {},
            onDeleteSelectedPlannedPost = {},
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
            onDateInputChange = {},
            onDateSelected = {},
            onCancelSelection = {},
            onConfirmSelection = {},
            onBackToDateSelection = {},
            onToggleFullBody = {},
            onTopAndOuterwearCentered = {},
            onBottomCentered = {},
            onFootwearCentered = {},
            onFullBodyCentered = {},
            onContinueToPlanningReview = {},
            onBackFromPlanningReview = {},
            onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {},
            onPlannedGarmentsChange = {},
            onSavePlanning = {},
            onSavedDialogContinue = {},
            onEditSelectedPlannedPost = {},
            onDeleteSelectedPlannedPost = {},
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
            uiState = previewState(selectedDate).let { state ->
                state.copy(
                    step = PlannerStep.PLANNING_REVIEW,
                    plannedGarments = state.currentCarouselGarments
                )
            },
            onDateInputChange = {},
            onDateSelected = {},
            onCancelSelection = {},
            onConfirmSelection = {},
            onBackToDateSelection = {},
            onToggleFullBody = {},
            onTopAndOuterwearCentered = {},
            onBottomCentered = {},
            onFootwearCentered = {},
            onFullBodyCentered = {},
            onContinueToPlanningReview = {},
            onBackFromPlanningReview = {},
            onAddMoreGarments = {},
            onPlannedOutfitTitleChange = {},
            onPlannedGarmentsChange = {},
            onSavePlanning = {},
            onSavedDialogContinue = {},
            onEditSelectedPlannedPost = {},
            onDeleteSelectedPlannedPost = {},
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

    val topAndOuterwear = garments.filter {
        it.category == GarmentCategory.TOP || it.category == GarmentCategory.OUTERWEAR
    }
    val bottoms = garments.filter { it.category == GarmentCategory.BOTTOM }
    val footwear = garments.filter { it.category == GarmentCategory.FOOTWEAR }
    val fullBody = garments.filter { it.category == GarmentCategory.FULL_BODY }

    return PlannerUiState(
        selectedDate = selectedDate,
        visibleMonth = YearMonth.from(selectedDate),
        dateInput = selectedDate.format(InputDateFormatter),
        forecastDays = listOf(
            PlannerForecastDay(selectedDate, WeatherCondition.MILD, 20, "Templado")
        ),
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

private fun previewGarment(
    imageName: String,
    name: String,
    category: GarmentCategory
): Garment = Garment(
    id = imageName,
    ownerUserId = "preview",
    name = name,
    category = category,
    imageUrl = "android.resource://com.closify.myapplication/drawable/$imageName",
    suitableWeather = setOf(WeatherCondition.ANY),
    suitableOccasions = setOf(Occasion.ANY)
)
