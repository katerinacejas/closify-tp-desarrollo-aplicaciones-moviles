package com.closify.myapplication

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import com.closify.myapplication.data.repository.AppearanceRepository
import com.closify.myapplication.navigation.AppNavGraph
import com.closify.myapplication.navigation.AuthNavGraph
import com.closify.myapplication.navigation.Screen
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.MainViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appearanceRepository = remember {
                AppearanceRepository.getInstance(applicationContext)
            }
            val isDarkMode by appearanceRepository.isDarkMode.collectAsStateWithLifecycle()
            val language by appearanceRepository.language.collectAsStateWithLifecycle()

            // Contexto base (Activity)
            val context = LocalContext.current
            
            // Buscamos la Activity real para obtener los Owners correctamente
            // Creamos un contexto localizado dinámicamente sin reiniciar la Activity
            val localizedContext = remember(language) {
                updateLocale(context, language)
            }

            // Inyectamos el contexto localizado pero preservamos los Owners originales de la Activity
            val activityOwner = remember(context) { 
                var currentContext = context
                while (currentContext is ContextWrapper) {
                    if (currentContext is ComponentActivity) break
                    currentContext = currentContext.baseContext
                }
                currentContext as ComponentActivity
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalLifecycleOwner provides activityOwner,
                LocalSavedStateRegistryOwner provides activityOwner,
                LocalActivityResultRegistryOwner provides activityOwner,
                LocalOnBackPressedDispatcherOwner provides activityOwner
            ) {
                ClosifyTheme(darkTheme = isDarkMode) {
                    val view = LocalView.current
                    SideEffect {
                        val window = (view.context as Activity).window
                        WindowCompat.getInsetsController(window, view).apply {
                            isAppearanceLightStatusBars = !isDarkMode
                            isAppearanceLightNavigationBars = !isDarkMode
                        }
                    }

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

    private fun updateLocale(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
