package com.closify.myapplication.ui.screens.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.ui.components.ClosifyButton
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.components.ClosifyTextField
import com.closify.myapplication.ui.screens.auth.GoogleSignInButton

@Composable
fun RegisterStep1Content(
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
    onGoogleSignIn: () -> Unit,
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
            text = stringResource(R.string.register_title_1),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.register_subtitle_1),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        ClosifyTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.register_username_placeholder),
            error = usernameError
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.login_email),
            error = emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.login_password),
            isPassword = true,
            error = passwordError
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClosifyTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(R.string.register_confirm_password_placeholder),
            isPassword = true,
            error = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(28.dp))

        ClosifyButton(
            text = stringResource(R.string.register_button),
            onClick = onNext,
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
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(stringResource(R.string.register_already_have_account))
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(stringResource(R.string.login_button))
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
