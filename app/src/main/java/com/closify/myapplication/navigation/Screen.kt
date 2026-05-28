package com.closify.myapplication.navigation

sealed class Screen(val route: String) {

    // — Auth —
    data object Onboarding : Screen("onboarding")
    data object Login      : Screen("login")
    data object Register   : Screen("register")

    // — App —
    data object Home         : Screen("home")
    data object OutfitResult : Screen("outfit_result")

    data object ClassifyGarment : Screen("classify_garment")

    // — Bottom Nav (placeholders) —
    data object Wardrobe  : Screen("wardrobe")
    data object Friends   : Screen("friends")
    data object Camera    : Screen("camera")
    data object Calendar  : Screen("calendar")
    data object Profile   : Screen("profile")
    data object Settings  : Screen("settings")
    data object EditProfile : Screen("edit_profile")
    data object Security : Screen("security")
}
