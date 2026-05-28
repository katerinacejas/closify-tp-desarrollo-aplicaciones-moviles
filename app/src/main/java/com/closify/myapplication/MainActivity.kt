package com.closify.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.navigation.AppNavGraph
import com.closify.myapplication.navigation.AuthNavGraph
import com.closify.myapplication.navigation.Screen
import com.closify.myapplication.ui.screens.settings.SettingsScreen
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClosifyTheme {
                val mainViewModel: MainViewModel = viewModel()
                val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()

                if (isLoggedIn) {
                    AppNavGraph(
                        onLogout = { mainViewModel.onLogout() }
                    )
                } else {
                    AuthNavGraph(
                        onLoginSuccess = { mainViewModel.onLoginSuccess() },
                        startDestination = if (mainViewModel.hasLoggedOutOnce) Screen.Login.route
                                           else Screen.Onboarding.route
                    )
                }
            }
        }
    }
}
