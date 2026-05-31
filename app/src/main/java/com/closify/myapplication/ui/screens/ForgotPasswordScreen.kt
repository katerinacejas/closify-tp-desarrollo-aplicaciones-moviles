package com.closify.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.screens.auth.AuthBrandHeader
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.ForgotPasswordEvent
import com.closify.myapplication.ui.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onRecoverySent: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.recoverySent) {
        if (uiState.recoverySent) {
            viewModel.onEvent(ForgotPasswordEvent.ResetSentState)
            onRecoverySent()
        }
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(ForgotPasswordEvent.ErrorDismissed)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ForgotPasswordContent(
                email = uiState.email,
                emailError = uiState.emailError,
                isLoading = uiState.isLoading,
                onEmailChange = { viewModel.onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                onSubmit = { viewModel.onEvent(ForgotPasswordEvent.Submit) }
            )
            AuthBackButton(onBack = onBack)
        }
    }
}

@Composable
fun PasswordRecoverySentScreen(
    onBack: () -> Unit,
    onGoToLogin: () -> Unit,
    onResend: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PasswordRecoverySentContent(
                onGoToLogin = onGoToLogin,
                onResend = onResend
            )
            AuthBackButton(onBack = onBack)
        }
    }
}

@Composable
private fun AuthBackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 12.dp, top = 18.dp),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    email: String,
    emailError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthBrandHeader()

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = "Restablecer contrase\u00F1a",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(46.dp))

        Text(
            text = "Ingres\u00E1 el email con el que te registraste\ny te enviaremos instrucciones para\nrestablecer tu contrase\u00F1a.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))

        ClosifyTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            keyboardType = KeyboardType.Email,
            error = emailError
        )

        Spacer(modifier = Modifier.height(44.dp))

        ClosifyButton(
            text = "Recuperar cuenta",
            onClick = onSubmit,
            isLoading = isLoading
        )
    }
}

@Composable
private fun PasswordRecoverySentContent(
    onGoToLogin: () -> Unit,
    onResend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthBrandHeader()

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = "Revis\u00E1 tu correo",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(46.dp))

        Text(
            text = "Si existe una cuenta asociada a ese email,\nte enviaremos un enlace para\nrestablecer tu contrase\u00F1a.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(56.dp))

        ClosifyButton(
            text = "Volver al inicio de sesi\u00F3n",
            onClick = onGoToLogin
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append("\u00BFNo recibiste el correo? ")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Reenviar")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onResend()
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ClosifyTheme {
        ForgotPasswordScreen(
            onBack = {},
            onRecoverySent = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PasswordRecoverySentScreenPreview() {
    ClosifyTheme {
        PasswordRecoverySentScreen(
            onBack = {},
            onGoToLogin = {},
            onResend = {}
        )
    }
}
