package com.closify.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.closify.myapplication.data.repository.AppearanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val appearanceRepository = AppearanceRepository.getInstance(application)

    val isDarkMode: StateFlow<Boolean> = appearanceRepository.isDarkMode

    private val _fontScale = MutableStateFlow(1f) // 0f, 1f, 2f, 3f para los 4 niveles
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _language = MutableStateFlow("ENGLISH")
    val language: StateFlow<String> = _language.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        appearanceRepository.setDarkMode(enabled)
    }

    fun updateFontScale(scale: Float) {
        _fontScale.value = scale
    }

    fun updateLanguage(lang: String) {
        _language.value = lang
    }
}
