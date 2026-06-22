package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.ui.theme.ClosifyTheme

@Composable
fun ClosifyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    trailingContent: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val visualTransformation = when {
        isPassword && !passwordVisible -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            isError = error != null,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            trailingIcon = {
                when {
                    isPassword -> {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility
                                              else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) stringResource(R.string.common_hide_password)
                                                     else stringResource(R.string.common_show_password),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    trailingContent != null -> trailingContent()
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor      = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                errorBorderColor        = MaterialTheme.colorScheme.error,
                errorContainerColor     = MaterialTheme.colorScheme.surface,
                focusedTextColor        = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor      = MaterialTheme.colorScheme.onBackground
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClosifyTextFieldPreview() {
    ClosifyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ClosifyTextField(
                value = "",
                onValueChange = {},
                placeholder = "Email"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClosifyTextFieldErrorPreview() {
    ClosifyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ClosifyTextField(
                value = "usuario",
                onValueChange = {},
                placeholder = "@Usuario",
                error = "Ese usuario ya está en uso. Elegí otro."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClosifyPasswordFieldPreview() {
    ClosifyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ClosifyTextField(
                value = "",
                onValueChange = {},
                placeholder = "Contraseña",
                isPassword = true
            )
        }
    }
}
