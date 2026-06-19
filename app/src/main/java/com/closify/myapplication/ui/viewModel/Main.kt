package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(UserRepository.instance.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    var hasLoggedOutOnce = false
        private set

    fun onLoginSuccess() {
        _isLoggedIn.value = true
    }

    fun onLogout() {
        UserRepository.instance.logout()
        hasLoggedOutOnce = true
        _isLoggedIn.value = false
    }
}
