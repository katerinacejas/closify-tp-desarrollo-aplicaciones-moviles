package com.closify.myapplication.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.closify.myapplication.R
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.TelemetryProvider

data class OnboardingPage(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int
)

class OnboardingViewModel(
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker
) : ViewModel() {

    val pages = listOf(
        OnboardingPage(
            imageRes       = R.drawable.onboarding_1,
            titleRes       = R.string.onboarding_1_title,
            descriptionRes = R.string.onboarding_1_description
        ),
        OnboardingPage(
            imageRes       = R.drawable.onboarding_2,
            titleRes       = R.string.onboarding_2_title,
            descriptionRes = R.string.onboarding_2_description
        ),
        OnboardingPage(
            imageRes       = R.drawable.onboarding_3,
            titleRes       = R.string.onboarding_3_title,
            descriptionRes = R.string.onboarding_3_description
        )
    )

    fun buttonLabelRes(currentPage: Int): Int =
        if (currentPage == pages.lastIndex) R.string.btn_start else R.string.btn_continue

    fun onButtonClick(currentPage: Int, goToNextPage: (Int) -> Unit) {
        if (currentPage == pages.lastIndex) {
            onFinish()
        } else {
            goToNextPage(currentPage + 1)
        }
    }

    private fun onFinish() {
        analyticsTracker.track(AnalyticsEvents.onboardingCompleted())
        // TODO: guardar en DataStore que el onboarding ya fue visto
        // TODO: navegar a home
    }
}
