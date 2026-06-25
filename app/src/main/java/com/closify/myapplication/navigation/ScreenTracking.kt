package com.closify.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

@Composable
fun TrackScreenViews(
    navController: NavHostController,
    analyticsTracker: AnalyticsTracker
) {
    LaunchedEffect(navController, analyticsTracker) {
        navController.currentBackStackEntryFlow
            .mapNotNull { it.destination.route }
            .distinctUntilChanged()
            .collect { route ->
                analyticsTracker.track(
                    AnalyticsEvents.screenViewed(
                        screenName = route.toAnalyticsScreenName(),
                        route = route
                    )
                )
            }
    }
}

private fun String.toAnalyticsScreenName(): String =
    when (this) {
        Screen.Onboarding.route -> "onboarding"
        Screen.Login.route -> "login"
        Screen.ForgotPassword.route -> "forgot_password"
        Screen.PasswordRecoverySent.route -> "password_recovery_sent"
        Screen.Register.route -> "register"
        Screen.Home.route -> "home"
        Screen.OutfitResult.route -> "outfit_result"
        Screen.SaveFavorites.route -> "save_favorites"
        Screen.Wardrobe.route -> "wardrobe"
        Screen.Friends.route -> "friends"
        Screen.Camera.route -> "add_garment"
        Screen.CameraPreview.route -> "camera_preview"
        Screen.ClassifyGarment.route -> "classify_garment"
        Screen.Calendar.route -> "planner"
        Screen.Profile.route -> "profile"
        Screen.Notifications.route -> "notifications"
        Screen.Settings.route -> "settings"
        Screen.FriendProfile.route -> "friend_profile"
        Screen.ProfilePost.route -> "profile_post"
        else -> when {
            startsWith("${Screen.Wardrobe.route}/category/") -> "wardrobe_category"
            startsWith("${Screen.Wardrobe.route}/weather/") -> "wardrobe_weather"
            startsWith("${Screen.Wardrobe.route}/occasion/") -> "wardrobe_occasion"
            startsWith("${Screen.Wardrobe.route}/detail/") -> "garment_detail"
            else -> replace("/", "_").replace("{", "").replace("}", "")
        }
    }
