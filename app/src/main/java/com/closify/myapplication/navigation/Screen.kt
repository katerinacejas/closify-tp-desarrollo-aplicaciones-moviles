package com.closify.myapplication.navigation

sealed class Screen(val route: String) {

    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object Home : Screen("home")
    data object OutfitResult : Screen("outfit_result")
    data object FriendProfile : Screen("friend_profile/{userId}") {
        const val ARG_USER_ID = "userId"

        fun createRoute(userId: String): String = "friend_profile/$userId"
    }

    data object Wardrobe : Screen("wardrobe")
    data object Friends : Screen("friends")
    data object Camera : Screen("camera")
    data object Calendar : Screen("calendar")
    data object Profile : Screen("profile")
}
