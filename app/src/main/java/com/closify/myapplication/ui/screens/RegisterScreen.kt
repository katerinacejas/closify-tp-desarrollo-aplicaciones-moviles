package com.closify.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.closify.myapplication.R
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.theme.ClosifyTheme
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
                onUsernameChange = { viewModel.onEvent(RegisterEvent.UsernameChanged(it)) },
                onEmailChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                onPasswordChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                onConfirmPasswordChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun RegisterStep1Content(
    username: String,
    email: String,
    password: String,
    confirmPassword: String,
    usernameError: String?,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNext: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        ClosifyLogo(size = 80.dp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Unite a Closify",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Redescubrí tu closet, simplificá tu día",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        ClosifyTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = "@Usuario",
            error = usernameError
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            error = emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Contraseña",
            isPassword = true,
            error = passwordError
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Confirmar Contraseña",
            isPassword = true,
            error = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(28.dp))

        ClosifyButton(
            text = "Registrarse",
            onClick = onNext,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append("¿Ya tenés una cuenta? ")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Iniciar Sesión")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onNavigateToLogin() }
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun RegisterStep2Content(
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

        ClosifyTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Nombre",
            error = nameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha de nacimiento
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

        // Biografía con contador
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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

@Composable
private fun DateInputField(
    day: String,
    month: String,
    year: String,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Día
            DateSegmentField(
                value = day,
                onValueChange = { if (it.length <= 2) onDayChange(it) },
                placeholder = "dd",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "/",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Mes
            DateSegmentField(
                value = month,
                onValueChange = { if (it.length <= 2) onMonthChange(it) },
                placeholder = "mm",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "/",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Año
            DateSegmentField(
                value = year,
                onValueChange = { if (it.length <= 4) onYearChange(it) },
                placeholder = "aaaa",
                modifier = Modifier.weight(2f)
            )
        }

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

@Composable
private fun DateSegmentField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
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
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor      = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor    = MaterialTheme.colorScheme.outline
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}

@Preview(showSystemUi = true)
@Composable
private fun RegisterStep1Preview() {
    ClosifyTheme {
        RegisterScreen(
            onRegisterSuccess = {},
            onNavigateToLogin = {},
            onBack = {}
        )
    }
}
