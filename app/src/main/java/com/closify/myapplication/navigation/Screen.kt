package com.closify.myapplication.navigation

sealed class Screen(val route: String) {

    // — Auth —
    data object Onboarding : Screen("onboarding")
    data object Login      : Screen("login")
    data object Register   : Screen("register")

    // — App —
    data object Home : Screen("home")
}
