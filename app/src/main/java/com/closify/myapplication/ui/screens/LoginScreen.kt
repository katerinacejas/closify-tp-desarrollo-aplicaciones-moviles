package com.closify.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import com.closify.myapplication.R
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.data.auth.GoogleCredentialProvider
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.screens.auth.AuthBrandHeader
import com.closify.myapplication.ui.screens.auth.GoogleSignInButton
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.LoginEvent
import com.closify.myapplication.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val googleCredentialProvider = remember(context) { GoogleCredentialProvider(context) }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) onLoginSuccess()
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(LoginEvent.ErrorDismissed)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LoginContent(
            email = uiState.email,
            password = uiState.password,
            emailError = uiState.emailError,
            passwordError = uiState.passwordError,
            isLoading = uiState.isLoading,
            onEmailChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
            onPasswordChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
            onSubmit = { viewModel.onEvent(LoginEvent.Submit) },
            onGoogleSignIn = {
                coroutineScope.launch {
                    googleCredentialProvider.getCredential()
                        .onSuccess { viewModel.onEvent(LoginEvent.GoogleSignInRequested(it)) }
                        .onFailure { viewModel.onEvent(LoginEvent.GoogleSignInFailed(it.message)) }
                }
            },
            onForgotPasswordClick = onNavigateToForgotPassword,
            onNavigateToRegister = {
                viewModel.onEvent(LoginEvent.ClearErrors)
                onNavigateToRegister()
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
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

        Spacer(modifier = Modifier.height(54.dp))

        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(52.dp))

        ClosifyTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.login_email),
            error = emailError
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.login_password),
            isPassword = true,
            error = passwordError
        )

        Spacer(modifier = Modifier.height(62.dp))

        ClosifyButton(
            text = stringResource(R.string.login_button),
            onClick = onSubmit,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoogleSignInButton(
            text = stringResource(R.string.auth_continue_google),
            onClick = onGoogleSignIn,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login_forgot_password),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onForgotPasswordClick()
            }
        )

        Spacer(modifier = Modifier.height(92.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(stringResource(R.string.login_no_account))
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(stringResource(R.string.login_register))
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onNavigateToRegister() }
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    ClosifyTheme {
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {}
        )
    }
}
