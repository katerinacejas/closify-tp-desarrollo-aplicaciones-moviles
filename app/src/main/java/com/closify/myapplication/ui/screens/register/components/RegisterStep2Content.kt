package com.closify.myapplication.ui.screens.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyLogo

@Composable
fun RegisterStep2Content(
    name: String,
    birthDay: String,
    birthMonth: String,
    birthYear: String,
    bio: String,
    nameError: String?,
    birthdateError: String?,
    bioError: String?,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onBirthDayChange: (String) -> Unit,
    onBirthMonthChange: (String) -> Unit,
    onBirthYearChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        ClosifyLogo(size = 64.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Contanos de vos",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "¡Queremos conocerte! Ya casi sos parte de Closify",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        com.closify.myapplication.ui.components.ClosifyTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Nombre",
            error = nameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Fecha de nacimiento",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        DateInputField(
            day = birthDay,
            month = birthMonth,
            year = birthYear,
            onDayChange = onBirthDayChange,
            onMonthChange = onBirthMonthChange,
            onYearChange = onBirthYearChange,
            error = birthdateError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 140) onBioChange(it) },
                placeholder = {
                    Text(
                        text = "Biografía",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = false,
                minLines = 4,
                maxLines = 6,
                isError = bioError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor      = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                    errorBorderColor        = MaterialTheme.colorScheme.error,
                    errorContainerColor     = MaterialTheme.colorScheme.surface
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bioError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "${bio.length}/140",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        ClosifyButton(
            text = "Continuar",
            onClick = onSubmit,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
