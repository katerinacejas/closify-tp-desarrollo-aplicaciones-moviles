package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _fontScale = MutableStateFlow(1f) // 0f, 1f, 2f, 3f para los 4 niveles
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _language = MutableStateFlow("ENGLISH")
    val language: StateFlow<String> = _language.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun updateFontScale(scale: Float) {
        _fontScale.value = scale
    }

    fun updateLanguage(lang: String) {
        _language.value = lang
    }
}
