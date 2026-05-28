package com.closify.myapplication.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.components.ClosifyTopBar
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.EditProfileViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val birthdate = remember(uiState.birthDate) { uiState.birthDate.toLocalDateOrNull() }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ClosifyTopBar(
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Editar Perfil",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Nombre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(4.dp))
            ClosifyTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onNameChange,
                placeholder = "Tu nombre",
                error = uiState.fullNameError
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(4.dp))
            ClosifyTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = "@usuario",
                error = uiState.usernameError
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Algo sobre ti..", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(4.dp))
            Box {
                ClosifyTextField(
                    value = uiState.bio,
                    onValueChange = viewModel::onBioChange,
                    placeholder = "Contanos algo sobre vos...",
                    minLines = 3,
                    maxLines = 3,
                    singleLine = false
                )
                Text(
                    text = "${uiState.bio.length}/140",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.bioError == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Fecha de nacimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(4.dp))

            val dateFormatter = remember { DateTimeFormatter.ofPattern("dd / MM / yyyy") }
            val birthdateText = birthdate?.format(dateFormatter) ?: uiState.birthDate.ifBlank { "dd / mm / aaaa" }

            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = birthdateText,
                        color = if (birthdate == null) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.generalError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ClosifyButton(
                text = "Guardar Cambios",
                onClick = viewModel::saveChanges
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = birthdate
                    ?.atStartOfDay(ZoneId.systemDefault())
                    ?.toInstant()
                    ?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.onBirthDateChange(date.toSpanishBirthDate())
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    ClosifyTheme {
        EditProfileScreen()
    }
}

private fun String.toLocalDateOrNull(): LocalDate? {
    val normalized = trim()
    if (normalized.isBlank()) return null

    runCatching {
        return LocalDate.parse(normalized, DateTimeFormatter.ofPattern("d/M/yyyy"))
    }

    runCatching {
        return LocalDate.parse(normalized, DateTimeFormatter.ofPattern("d / MM / yyyy"))
    }

    val parts = normalized.split(" de ")
    if (parts.size != 3) return null

    val day = parts[0].toIntOrNull() ?: return null
    val month = when (parts[1].lowercase()) {
        "enero" -> 1
        "febrero" -> 2
        "marzo" -> 3
        "abril" -> 4
        "mayo" -> 5
        "junio" -> 6
        "julio" -> 7
        "agosto" -> 8
        "septiembre" -> 9
        "octubre" -> 10
        "noviembre" -> 11
        "diciembre" -> 12
        else -> return null
    }
    val year = parts[2].toIntOrNull() ?: return null

    return runCatching { LocalDate.of(year, month, day) }.getOrNull()
}

private fun LocalDate.toSpanishBirthDate(): String {
    val monthName = when (monthValue) {
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
        12 -> "diciembre"
        else -> ""
    }

    return "$dayOfMonth de $monthName de $year"
}
