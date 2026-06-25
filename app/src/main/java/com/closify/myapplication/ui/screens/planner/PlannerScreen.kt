package com.closify.myapplication.ui.screens.planner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.location.rememberDeviceLocationRequester
import com.closify.myapplication.ui.viewmodel.PlannerViewModel

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val requestDeviceLocation = rememberDeviceLocationRequester(
        onLocationAvailable = viewModel::onForecastLocationAvailable,
        onLocationUnavailable = viewModel::onForecastUnavailable
    )

    LaunchedEffect(Unit) {
        requestDeviceLocation()
    }

    PlannerContent(
        uiState = uiState,
        onDateInputChange = viewModel::onDateInputChange,
        onDateSelected = viewModel::onDateSelected,
        onCancelSelection = viewModel::onCancelSelection,
        onConfirmSelection = viewModel::onConfirmSelection,
        onBackToDateSelection = viewModel::onBackToDateSelection,
        onToggleFullBody = viewModel::onToggleFullBody,
        onTopAndOuterwearCentered = viewModel::onTopAndOuterwearCentered,
        onBottomCentered = viewModel::onBottomCentered,
        onFootwearCentered = viewModel::onFootwearCentered,
        onFullBodyCentered = viewModel::onFullBodyCentered,
        onContinueToPlanningReview = viewModel::onContinueToPlanningReview,
        onBackFromPlanningReview = viewModel::onBackFromPlanningReview,
        onAddMoreGarments = viewModel::onAddMoreGarments,
        onPlannedOutfitTitleChange = viewModel::onPlannedOutfitTitleChange,
        onPlannedGarmentsChange = viewModel::onPlannedGarmentsChange,
        onSavePlanning = viewModel::onSavePlanning,
        onSavedDialogContinue = viewModel::onSavedDialogContinue,
        onEditSelectedPlannedPost = viewModel::onEditSelectedPlannedPost,
        onDeleteSelectedPlannedPost = viewModel::onDeleteSelectedPlannedPost,
        onDismissSelectedPlannedPost = viewModel::onDismissSelectedPlannedPost,
        onDismissNoFullBodyDialog = viewModel::onDismissNoFullBodyDialog
    )
}
