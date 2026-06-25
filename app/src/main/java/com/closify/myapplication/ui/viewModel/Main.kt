package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(UserRepository.instance.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    var hasLoggedOutOnce = false
        private set

    init {
        if (UserRepository.instance.isLoggedIn()) {
            viewModelScope.launch {
                UserRepository.instance.restoreSession()
            }
        }
    }

    fun onLoginSuccess() {
        _isLoggedIn.value = true
    }

    fun onLogout() {
        UserRepository.instance.logout()
        OutfitPostRepository.instance.resetSessionSync()
        SocialRepository.instance.resetSessionSync()
        NotificationRepository.instance.resetSessionSync()
        hasLoggedOutOnce = true
        _isLoggedIn.value = false
    }
}
