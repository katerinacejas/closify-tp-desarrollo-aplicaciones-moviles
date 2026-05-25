package com.closify.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.closify.myapplication.ui.screens.home.HomeScreen
import com.closify.myapplication.ui.screens.outfitresult.OutfitResultScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
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
    }
}
