package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    var hasLoggedOutOnce = false
        private set

    fun onLoginSuccess() {
        _isLoggedIn.value = true
    }

    fun onLogout() {
        hasLoggedOutOnce = true
        _isLoggedIn.value = false
    }
}
