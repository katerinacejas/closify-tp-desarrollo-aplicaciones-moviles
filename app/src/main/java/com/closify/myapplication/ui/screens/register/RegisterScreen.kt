package com.closify.myapplication.ui.screens.register

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.screens.register.components.RegisterStep1Content
import com.closify.myapplication.ui.screens.register.components.RegisterStep2Content
import com.closify.myapplication.ui.screens.register.components.RegisterTopBar
import com.closify.myapplication.ui.viewmodel.RegisterEvent
import com.closify.myapplication.ui.viewmodel.RegisterStep
import com.closify.myapplication.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) onRegisterSuccess()
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(RegisterEvent.ErrorDismissed)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RegisterTopBar(
                onBack = {
                    if (uiState.currentStep == RegisterStep.STEP_2) {
                        viewModel.onEvent(RegisterEvent.GoBack)
                    } else {
                        onBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState.currentStep) {
            RegisterStep.STEP_1 -> RegisterStep1Content(
                username = uiState.username,
                email = uiState.email,
                password = uiState.password,
                confirmPassword = uiState.confirmPassword,
                usernameError = uiState.usernameError,
                emailError = uiState.emailError,
                passwordError = uiState.passwordError,
                confirmPasswordError = uiState.confirmPasswordError,
                isLoading = uiState.isLoading,
                acceptedTerms = uiState.acceptedTerms,
                onUsernameChange = { viewModel.onEvent(RegisterEvent.UsernameChanged(it)) },
                onEmailChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                onPasswordChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                onConfirmPasswordChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                onTermsToggle = { viewModel.onEvent(RegisterEvent.TermsToggled(it)) },
                onNext = { viewModel.onEvent(RegisterEvent.NextStep) },
                onNavigateToLogin = onNavigateToLogin,
                modifier = Modifier.padding(innerPadding)
            )
            RegisterStep.STEP_2 -> RegisterStep2Content(
                name = uiState.name,
                birthDay = uiState.birthDay,
                birthMonth = uiState.birthMonth,
                birthYear = uiState.birthYear,
                bio = uiState.bio,
                nameError = uiState.nameError,
                birthdateError = uiState.birthdateError,
                bioError = uiState.bioError,
                isLoading = uiState.isLoading,
                onNameChange = { viewModel.onEvent(RegisterEvent.NameChanged(it)) },
                onBirthDayChange = { viewModel.onEvent(RegisterEvent.BirthDayChanged(it)) },
                onBirthMonthChange = { viewModel.onEvent(RegisterEvent.BirthMonthChanged(it)) },
                onBirthYearChange = { viewModel.onEvent(RegisterEvent.BirthYearChanged(it)) },
                onBioChange = { viewModel.onEvent(RegisterEvent.BioChanged(it)) },
                onSubmit = { viewModel.onEvent(RegisterEvent.Submit) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
