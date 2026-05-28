package com.closify.myapplication.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.theme.ClosifyTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {}
) {
    // Estados internos mockeados para asegurar que funcione sin dependencias externas
    var name by remember { mutableStateOf("Katerina") }
    var username by remember { mutableStateOf("katerina_closify") }
    var bio by remember { mutableStateOf("Amante de la moda y el orden.") }
    var birthdate by remember { mutableStateOf<LocalDate?>(LocalDate.of(2000, 5, 15)) }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ClosifyLogo(size = 48.dp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = { Spacer(modifier = Modifier.size(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp), // Reducido de 24 a 20
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Reducido de 16 a 8

            Text(
                text = "Editar Perfil",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp), // Fuente un pelín más chica
                color = Color(0xFF333333),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp)) // Reducido de 32 a 16

            // 1. Título Nombre y caja
            Text(text = "Nombre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp)) // Reducido de 8 a 4
            ClosifyTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Tu nombre"
            )

            Spacer(modifier = Modifier.height(12.dp)) // Reducido de 24 a 12

            // 2. Título Usuario y caja con @
            Text(text = "Usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            ClosifyTextField(
                value = if (username.startsWith("@")) username else "@$username",
                onValueChange = { 
                    val input = it.removePrefix("@")
                    username = "@$input"
                },
                placeholder = "@usuario"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Título Algo sobre ti... (caja grande con contador)
            Text(text = "Algo sobre ti..", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Box {
                ClosifyTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 140) bio = it },
                    placeholder = "Contanos algo sobre vos...",
                    minLines = 3, // Reducido de 4 a 3 para ahorrar espacio
                    maxLines = 3,
                    singleLine = false
                )
                Text(
                    text = "${bio.length}/140",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Título Fecha de nacimiento y caja con calendario
            Text(text = "Fecha de nacimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            
            val dateFormatter = remember { DateTimeFormatter.ofPattern("dd / MM / yyyy") }
            val birthdateText = birthdate?.format(dateFormatter) ?: "dd / mm / aaaa"

            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(), // Padding interno de 16 a 12
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = birthdateText, color = if (birthdate == null) Color.LightGray else Color.Black)
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Reducido de 24 a 16

            // Botón Guardar Cambios
            ClosifyButton(
                text = "Guardar Cambios",
                onClick = { onBack() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Calendario Material 3
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = birthdate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            birthdate = date
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
