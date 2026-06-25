package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closify.myapplication.core.telemetry.AnalyticsEvents
import com.closify.myapplication.core.telemetry.AnalyticsTracker
import com.closify.myapplication.core.telemetry.CrashReporter
import com.closify.myapplication.core.telemetry.TelemetryProvider
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.OutfitPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiItem(
    val notification: Notification,
    val post: OutfitPost? = null,
    val friendRequest: FriendRequest? = null
)

data class NotificationsUiState(
    val notifications: List<NotificationUiItem> = emptyList()
)

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository = NotificationRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance,
    private val analyticsTracker: AnalyticsTracker = TelemetryProvider.analyticsTracker,
    private val crashReporter: CrashReporter = TelemetryProvider.crashReporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            if (userId.isNotBlank()) {
                notificationRepository.syncNotifications(userId)
            }
            loadNotifications()
            markAllAsRead()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val userId = userRepository.currentUserId
            if (userId.isNotBlank()) {
                notificationRepository.syncNotifications(userId)
            }
            loadNotifications()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserOrDefault().id
            val notifications = notificationRepository.getNotifications(currentUserId)
            
            val items = mutableListOf<NotificationUiItem>()
            for (notification in notifications) {
                val post = notification.postId?.let { outfitPostRepository.getPost(it) }
                val request = notification.friendRequestId?.let { socialRepository.getFriendRequest(it) }
                items.add(NotificationUiItem(notification, post, request))
            }
            
            _uiState.update { it.copy(notifications = items) }
        }
    }

    fun onAcceptFriendRequest(requestId: String) {
        viewModelScope.launch {
            socialRepository.respondToFriendRequest(requestId, accepted = true)
                .onSuccess { analyticsTracker.track(AnalyticsEvents.friendRequestResponded(accepted = true)) }
                .onFailure { error ->
                    crashReporter.recordException(
                        throwable = error,
                        keys = mapOf("feature" to "notifications", "operation" to "accept_friend_request")
                    )
                }
            refresh()
        }
    }

    fun onRejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            socialRepository.respondToFriendRequest(requestId, accepted = false)
                .onSuccess { analyticsTracker.track(AnalyticsEvents.friendRequestResponded(accepted = false)) }
                .onFailure { error ->
                    crashReporter.recordException(
                        throwable = error,
                        keys = mapOf("feature" to "notifications", "operation" to "reject_friend_request")
                    )
                }
            refresh()
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserOrDefault().id
            notificationRepository.markAllAsRead(currentUserId)
        }
    }
}
