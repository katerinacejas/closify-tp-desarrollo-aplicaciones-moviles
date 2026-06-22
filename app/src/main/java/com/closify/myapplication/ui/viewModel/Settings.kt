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
    val language: StateFlow<String> = appearanceRepository.language

    private val _fontScale = MutableStateFlow(1f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        appearanceRepository.setDarkMode(enabled)
    }

    fun updateFontScale(scale: Float) {
        _fontScale.value = scale
    }

    fun updateLanguage(code: String) {
        appearanceRepository.setLanguage(code)
    }
}
