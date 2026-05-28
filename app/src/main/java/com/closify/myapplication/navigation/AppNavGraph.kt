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
import com.closify.myapplication.ui.screens.friends.FriendsScreen
import com.closify.myapplication.ui.screens.home.HomeScreen
import com.closify.myapplication.ui.screens.outfitresult.OutfitResultScreen
import com.closify.myapplication.ui.screens.profile.ProfileScreen
import com.closify.myapplication.ui.screens.publicprofile.PublicProfileScreen

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
                    if (currentRoute == screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(screen.route) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    } else {
                        if (currentRoute == Screen.OutfitResult.route) {
                            navController.popBackStack()
                        }
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = false }
                            launchSingleTop = true
                            restoreState = false
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

            composable(Screen.Wardrobe.route) { PlaceholderScreen("Guardarropa") }
            composable(Screen.Friends.route) {
                FriendsScreen(
                    onNotificationsClick = {
                        /* TODO: Implementar pantalla de notificaciones */
                    },
                    onOpenUserProfile = { userId ->
                        navController.navigate(Screen.FriendProfile.createRoute(userId))
                    }
                )
            }
            composable(Screen.FriendProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString(Screen.FriendProfile.ARG_USER_ID).orEmpty()
                PublicProfileScreen(
                    userId = userId,
                    onOpenUserProfile = { nextUserId ->
                        navController.navigate(Screen.FriendProfile.createRoute(nextUserId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Camera.route) { PlaceholderScreen("Camara") }
            composable(Screen.Calendar.route) { PlaceholderScreen("Calendario") }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = {
                        /* TODO: Implementar navegacion a configuracion */
                    },
                    onOpenUserProfile = { userId ->
                        navController.navigate(Screen.FriendProfile.createRoute(userId))
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
            text = "$name proximamente",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
