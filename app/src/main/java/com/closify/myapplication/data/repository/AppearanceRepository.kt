package com.closify.myapplication.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppearanceRepository private constructor(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val _isDarkMode = MutableStateFlow(
        preferences.getBoolean(KEY_DARK_MODE, false)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
        _isDarkMode.value = enabled
    }

    companion object {
        private const val PREFERENCES_NAME = "closify_appearance_preferences"
        private const val KEY_DARK_MODE = "dark_mode_enabled"

        @Volatile
        private var instance: AppearanceRepository? = null

        fun getInstance(context: Context): AppearanceRepository =
            instance ?: synchronized(this) {
                instance ?: AppearanceRepository(context).also { instance = it }
            }
    }
}
