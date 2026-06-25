package com.closify.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.ui.screens.ForgotPasswordScreen
import com.closify.myapplication.ui.screens.LoginScreen
import com.closify.myapplication.ui.screens.OnboardingScreen
import com.closify.myapplication.ui.screens.PasswordRecoverySentScreen
import com.closify.myapplication.ui.screens.register.RegisterScreen

@Composable
fun AuthNavGraph(
    onLoginSuccess: () -> Unit,
    startDestination: String = Screen.Onboarding.route,
    navController: NavHostController = rememberNavController(),
    analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) {
    TrackScreenViews(navController = navController, analyticsTracker = analyticsTracker)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        // Limpia el onboarding del back stack — no puede volver con el botón atrás
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onRecoverySent = {
                    navController.navigate(Screen.PasswordRecoverySent.route)
                }
            )
        }

        composable(Screen.PasswordRecoverySent.route) {
            PasswordRecoverySentScreen(
                onBack = { navController.popBackStack() },
                onGoToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onResend = {
                    navController.popBackStack(Screen.ForgotPassword.route, inclusive = false)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = onLoginSuccess,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
