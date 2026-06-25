package com.closify.myapplication.core.telemetry

data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any?> = emptyMap()
)

object AnalyticsEvents {
    fun screenViewed(screenName: String, route: String): AnalyticsEvent =
        AnalyticsEvent(
            name = "screen_view",
            parameters = mapOf(
                "firebase_screen" to screenName,
                "firebase_screen_class" to route
            )
        )

    fun onboardingCompleted(): AnalyticsEvent =
        AnalyticsEvent("onboarding_completed")

    fun loginSubmitted(): AnalyticsEvent =
        AnalyticsEvent("login_submitted", mapOf("method" to "email"))

    fun loginSucceeded(): AnalyticsEvent =
        AnalyticsEvent("login_success", mapOf("method" to "email"))

    fun loginFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("login_failed", mapOf("method" to "email", "reason" to reason.safeReason()))

    fun registerStepCompleted(step: Int): AnalyticsEvent =
        AnalyticsEvent("register_step_completed", mapOf("step" to step))

    fun registerSubmitted(): AnalyticsEvent =
        AnalyticsEvent("register_submitted", mapOf("method" to "email"))

    fun registerSucceeded(): AnalyticsEvent =
        AnalyticsEvent("register_success", mapOf("method" to "email"))

    fun registerFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("register_failed", mapOf("method" to "email", "reason" to reason.safeReason()))

    fun logout(): AnalyticsEvent =
        AnalyticsEvent("logout")

    fun automaticWeatherLoaded(weather: String): AnalyticsEvent =
        AnalyticsEvent("automatic_weather_loaded", mapOf("weather" to weather.normalized()))

    fun automaticWeatherFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("automatic_weather_failed", mapOf("reason" to reason.safeReason()))

    fun manualWeatherSelected(weather: String): AnalyticsEvent =
        AnalyticsEvent("manual_weather_selected", mapOf("weather" to weather.normalized()))

    fun occasionSelected(occasion: String): AnalyticsEvent =
        AnalyticsEvent("occasion_selected", mapOf("occasion" to occasion.normalized()))

    fun outfitGenerationRequested(weather: String, occasion: String, weatherMode: String): AnalyticsEvent =
        AnalyticsEvent(
            "outfit_generation_requested",
            mapOf(
                "weather" to weather.normalized(),
                "occasion" to occasion.normalized(),
                "weather_mode" to weatherMode.normalized()
            )
        )

    fun outfitGenerated(weather: String, occasion: String, resultCount: Int): AnalyticsEvent =
        AnalyticsEvent(
            "outfit_generated",
            mapOf(
                "weather" to weather.normalized(),
                "occasion" to occasion.normalized(),
                "result_count" to resultCount
            )
        )

    fun outfitGenerationFailed(reason: String, weather: String?, occasion: String?): AnalyticsEvent =
        AnalyticsEvent(
            "outfit_generation_failed",
            mapOf(
                "reason" to reason.normalized(),
                "weather" to weather?.normalized(),
                "occasion" to occasion?.normalized()
            )
        )

    fun favoriteSelectionChanged(selectedCount: Int): AnalyticsEvent =
        AnalyticsEvent("favorite_selection_changed", mapOf("selected_count" to selectedCount))

    fun favoriteOutfitsSaved(count: Int): AnalyticsEvent =
        AnalyticsEvent("favorite_outfits_saved", mapOf("count" to count))

    fun garmentInputSelected(source: String): AnalyticsEvent =
        AnalyticsEvent("garment_input_selected", mapOf("source" to source.normalized()))

    fun garmentSaved(category: String, weatherCount: Int, occasionCount: Int): AnalyticsEvent =
        AnalyticsEvent(
            "garment_saved",
            mapOf(
                "category" to category.normalized(),
                "weather_count" to weatherCount,
                "occasion_count" to occasionCount
            )
        )

    fun garmentDeleted(): AnalyticsEvent =
        AnalyticsEvent("garment_deleted")

    fun wardrobeFilterSelected(filter: String): AnalyticsEvent =
        AnalyticsEvent("wardrobe_filter_selected", mapOf("filter" to filter.normalized()))

    fun plannerDateConfirmed(daysFromToday: Long): AnalyticsEvent =
        AnalyticsEvent("planner_date_confirmed", mapOf("days_from_today" to daysFromToday))

    fun plannerSaved(garmentCount: Int, editingExisting: Boolean): AnalyticsEvent =
        AnalyticsEvent(
            "planner_outfit_saved",
            mapOf(
                "garment_count" to garmentCount,
                "editing_existing" to editingExisting
            )
        )

    fun friendRequestSent(surface: String): AnalyticsEvent =
        AnalyticsEvent("friend_request_sent", mapOf("surface" to surface.normalized()))

    fun friendRemoved(surface: String): AnalyticsEvent =
        AnalyticsEvent("friend_removed", mapOf("surface" to surface.normalized()))

    fun friendRequestResponded(accepted: Boolean): AnalyticsEvent =
        AnalyticsEvent("friend_request_responded", mapOf("accepted" to accepted))

    fun postLiked(surface: String): AnalyticsEvent =
        AnalyticsEvent("post_liked", mapOf("surface" to surface.normalized()))

    fun commentSent(surface: String): AnalyticsEvent =
        AnalyticsEvent("comment_sent", mapOf("surface" to surface.normalized()))

    fun postTitleUpdated(): AnalyticsEvent =
        AnalyticsEvent("post_title_updated")

    fun postDeleted(): AnalyticsEvent =
        AnalyticsEvent("post_deleted")

    fun passwordRecoveryRequested(): AnalyticsEvent =
        AnalyticsEvent("password_recovery_requested")

    fun passwordRecoveryFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("password_recovery_failed", mapOf("reason" to reason.safeReason()))

    fun passwordChanged(): AnalyticsEvent =
        AnalyticsEvent("password_changed")

    fun passwordChangeFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("password_change_failed", mapOf("reason" to reason.safeReason()))

    fun profileUpdated(): AnalyticsEvent =
        AnalyticsEvent("profile_updated")

    fun profileUpdateFailed(reason: String?): AnalyticsEvent =
        AnalyticsEvent("profile_update_failed", mapOf("reason" to reason.safeReason()))

    private fun String.normalized(): String = lowercase()

    private fun String?.safeReason(): String =
        this?.take(80)?.ifBlank { null } ?: "unknown"
}
