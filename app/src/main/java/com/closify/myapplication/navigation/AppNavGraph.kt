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
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.ui.screens.home.HomeScreen
import com.closify.myapplication.ui.screens.outfitresult.OutfitResultScreen
import com.closify.myapplication.ui.screens.profile.ProfileScreen
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.closify.myapplication.ui.screens.settings.SettingsScreen
import com.closify.myapplication.ui.screens.wardrobe.GarmentDetailScreen
import com.closify.myapplication.ui.screens.wardrobe.WardrobeDetailScreen
import com.closify.myapplication.ui.screens.wardrobe.WardrobeScreen

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

            // — Wardrobe —
            composable(Screen.Wardrobe.route) {
                WardrobeScreen(
                    onCategoryClick = { category ->
                        navController.navigate("${Screen.Wardrobe.route}/category/${category.name}")
                    },
                    onWeatherClick = { weather ->
                        navController.navigate("${Screen.Wardrobe.route}/weather/${weather.name}")
                    },
                    onOccasionClick = { occasion ->
                        navController.navigate("${Screen.Wardrobe.route}/occasion/${occasion.name}")
                    },
                    onGarmentClick = { garmentId ->
                        navController.navigate("${Screen.Wardrobe.route}/detail/$garmentId")
                    }
                )
            }

            composable("${Screen.Wardrobe.route}/category/{categoryName}") { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName")
                val category = categoryName?.let { GarmentCategory.valueOf(it) } ?: GarmentCategory.TOP
                WardrobeDetailScreen(
                    category = category,
                    onBack = { navController.popBackStack() },
                    onGarmentClick = { garmentId ->
                        navController.navigate("${Screen.Wardrobe.route}/detail/$garmentId")
                    }
                )
            }

            composable("${Screen.Wardrobe.route}/weather/{weatherName}") { backStackEntry ->
                val weatherName = backStackEntry.arguments?.getString("weatherName")
                val weather = weatherName?.let { WeatherCondition.valueOf(it) } ?: WeatherCondition.HOT
                WardrobeDetailScreen(
                    weather = weather,
                    onBack = { navController.popBackStack() },
                    onGarmentClick = { garmentId ->
                        navController.navigate("${Screen.Wardrobe.route}/detail/$garmentId")
                    }
                )
            }

            composable("${Screen.Wardrobe.route}/occasion/{occasionName}") { backStackEntry ->
                val occasionName = backStackEntry.arguments?.getString("occasionName")
                val occasion = occasionName?.let { Occasion.valueOf(it) } ?: Occasion.CASUAL
                WardrobeDetailScreen(
                    occasion = occasion,
                    onBack = { navController.popBackStack() },
                    onGarmentClick = { garmentId ->
                        navController.navigate("${Screen.Wardrobe.route}/detail/$garmentId")
                    }
                )
            }

            composable("${Screen.Wardrobe.route}/detail/{garmentId}") { backStackEntry ->
                val garmentId = backStackEntry.arguments?.getString("garmentId") ?: ""
                GarmentDetailScreen(
                    garmentId = garmentId,
                    onBack = { navController.popBackStack() }
                )
            }

            // — Placeholders —
            composable(Screen.Friends.route)   { PlaceholderScreen("Amigos") }
            composable(Screen.Camera.route)    { PlaceholderScreen("Cámara") }
            composable(Screen.Calendar.route)  { PlaceholderScreen("Calendario") }
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
