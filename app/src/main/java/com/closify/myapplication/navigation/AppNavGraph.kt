package com.closify.myapplication.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.closify.myapplication.ui.components.BottomNavBar
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.closify.myapplication.ui.screens.camera.CameraScreen
import com.closify.myapplication.ui.screens.camera.ClassifyGarmentScreen
import com.closify.myapplication.ui.screens.home.HomeScreen
import com.closify.myapplication.ui.screens.outfitresult.OutfitResultScreen
import com.closify.myapplication.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemSelected = { screen ->
                    // Si estamos en OutfitResult primero volvemos atrás
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

            // — Placeholders —
            composable(Screen.Wardrobe.route)  { PlaceholderScreen("Guardarropa") }
            composable(Screen.Friends.route)   { PlaceholderScreen("Amigos") }
            composable(Screen.Camera.route) {
                CameraScreen(
                    onNavigateToClassify = { uri ->
                        navController.navigate(Screen.ClassifyGarment.createRoute(uri))
                    }
                )
            }

            composable(
                route = Screen.ClassifyGarment.route,
                arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
                ClassifyGarmentScreen(
                    imageUri = imageUri,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Calendar.route)  { PlaceholderScreen("Calendario") }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = {
                        /* TODO: Implementar navegación a configuración */
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
