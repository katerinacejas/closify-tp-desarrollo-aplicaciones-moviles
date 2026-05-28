package com.closify.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.OutfitPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val socialRepository: SocialRepository = SocialRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        refresh()
        markAllAsRead()
    }

    fun refresh() {
        val currentUserId = userRepository.getCurrentUserOrDefault().id
        _uiState.value = NotificationsUiState(
            notifications = notificationRepository.getNotifications(currentUserId).map { notification ->
                NotificationUiItem(
                    notification = notification,
                    post = notification.postId?.let(notificationRepository::getPost),
                    friendRequest = notification.friendRequestId?.let(socialRepository::getFriendRequest)
                )
            }
        )
    }

    fun onAcceptFriendRequest(requestId: String) {
        socialRepository.respondToFriendRequest(requestId, accepted = true)
        refresh()
    }

    fun onRejectFriendRequest(requestId: String) {
        socialRepository.respondToFriendRequest(requestId, accepted = false)
        refresh()
    }

    private fun markAllAsRead() {
        val currentUserId = userRepository.getCurrentUserOrDefault().id
        notificationRepository.markAllAsRead(currentUserId)
        refresh()
    }
}
