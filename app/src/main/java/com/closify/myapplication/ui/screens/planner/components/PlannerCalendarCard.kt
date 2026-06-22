package com.closify.myapplication.ui.screens.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.viewmodel.PlannerUiState
import java.time.LocalDate
import java.time.YearMonth

internal fun calendarDaysFor(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val sundayBasedOffset = firstDay.dayOfWeek.value % 7
    val gridStart = firstDay.minusDays(sundayBasedOffset.toLong())
    return List(42) { index -> gridStart.plusDays(index.toLong()) }
}

internal fun LocalDate.toSpanishTitle(): String {
    val month = when (monthValue) {
        1 -> "enero"; 2 -> "febrero"; 3 -> "marzo"; 4 -> "abril"
        5 -> "mayo"; 6 -> "junio"; 7 -> "julio"; 8 -> "agosto"
        9 -> "septiembre"; 10 -> "octubre"; 11 -> "noviembre"
        else -> "diciembre"
    }
    return "$dayOfMonth de $month de $year"
}

@Composable
internal fun PlannerDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(text = "Fecha", style = MaterialTheme.typography.bodySmall)
        },
        trailingIcon = {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp))
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
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.height(56.dp)
    )
}

@Composable
internal fun PlannerCalendarCard(
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
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
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
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "Cancelar", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onConfirmSelection,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "Confirmar", style = MaterialTheme.typography.labelSmall)
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
            modifier = Modifier.size(38.dp).then(
                when {
                    isSelected       -> Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                    hasPlannedOutfit -> Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    else             -> Modifier
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
            weather?.let { WeatherIcon(weather = it, modifier = Modifier.padding(top = 1.dp)) }
        }
    }
}
