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

    data object ProfilePost : Screen("profile_post/{postId}") {
        const val ARG_POST_ID = "postId"

        fun createRoute(postId: String): String = "profile_post/$postId"
    }

    data object ClassifyGarment : Screen("classify_garment")
    data object SaveFavorites : Screen("save_favorites")

    data object Wardrobe : Screen("wardrobe")
    data object Friends : Screen("friends")
    data object Camera : Screen("camera")
    data object Calendar : Screen("calendar")
    data object Profile : Screen("profile")
    data object Notifications : Screen("notifications")
    data object Settings : Screen("settings")
    data object EditProfile : Screen("edit_profile")
    data object Security : Screen("security")
}
