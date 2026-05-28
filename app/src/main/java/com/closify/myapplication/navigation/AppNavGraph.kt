package com.closify.myapplication.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.closify.myapplication.ui.components.BottomNavBar
import com.closify.myapplication.ui.screens.camera.CameraScreen
import com.closify.myapplication.ui.screens.camera.ClassifyGarmentScreen
import com.closify.myapplication.ui.screens.home.HomeScreen
import com.closify.myapplication.ui.screens.outfitresult.OutfitResultScreen
import com.closify.myapplication.ui.screens.profile.ProfileScreen
import com.closify.myapplication.ui.viewmodel.CameraViewModel
import com.closify.myapplication.ui.screens.settings.SettingsScreen

private const val CAMERA_FLOW_ROUTE = "camera_flow"


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemSelected = { screen ->
                    if (currentRoute == Screen.OutfitResult.route) {
                        navController.popBackStack()
                    }
                    if (screen.route != Screen.Home.route || currentRoute != Screen.Home.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToOutfitResult = {
                        navController.navigate(Screen.OutfitResult.route)
                    }
                )
            }

            composable(Screen.OutfitResult.route) {
                OutfitResultScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Wardrobe.route)  { PlaceholderScreen("Guardarropa") }
            composable(Screen.Friends.route)   { PlaceholderScreen("Amigos") }
            composable(Screen.Calendar.route)  { PlaceholderScreen("Calendario") }

            navigation(
                startDestination = Screen.Camera.route,
                route = CAMERA_FLOW_ROUTE
            ) {
                composable(Screen.Camera.route) { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry(CAMERA_FLOW_ROUTE)
                    }
                    val cameraViewModel: CameraViewModel = viewModel(parentEntry)
                    CameraScreen(
                        viewModel = cameraViewModel,
                        onNavigateToClassify = {
                            navController.navigate(Screen.ClassifyGarment.route)
                        }
                    )
                }

                composable(Screen.ClassifyGarment.route) { entry ->
                    val parentEntry = remember(entry) {
                        navController.getBackStackEntry(CAMERA_FLOW_ROUTE)
                    }
                    val cameraViewModel: CameraViewModel = viewModel(parentEntry)
                    val cameraUiState by cameraViewModel.uiState.collectAsStateWithLifecycle()
                    ClassifyGarmentScreen(
                        imageUri = cameraUiState.selectedImageUri,
                        onBack = { navController.popBackStack() },
                        onSaved = {
                            navController.navigate(Screen.Wardrobe.route) {
                                popUpTo(CAMERA_FLOW_ROUTE) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = onLogout,
                    onBackToHome = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name — próximamente",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
