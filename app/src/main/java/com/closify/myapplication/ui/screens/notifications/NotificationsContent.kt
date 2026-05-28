package com.closify.myapplication.ui.screens.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.NotificationType
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.viewmodel.NotificationUiItem
import com.closify.myapplication.ui.viewmodel.NotificationsUiState

@Composable
fun NotificationsContent(
    uiState: NotificationsUiState,
    onOpenUserProfile: (String) -> Unit,
    onOpenPostInProfile: (String) -> Unit,
    onBackClick: () -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            NotificationsTopBar(
                searchQuery = "",
                onSearchQueryChange = {},
                onBackClick = onBackClick
            )
        }

        item {
            if (uiState.notifications.isEmpty()) {
                EmptyNotificationsCard(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 68.dp)
                )
            } else {
                NotificationsCard(
                    notifications = uiState.notifications,
                    onOpenUserProfile = onOpenUserProfile,
                    onOpenPostInProfile = onOpenPostInProfile,
                    onAcceptFriendRequest = onAcceptFriendRequest,
                    onRejectFriendRequest = onRejectFriendRequest,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(586.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Aún no tenés ninguna notificación",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "¡Interactuá con tus amigos para disfrutar de\nsus outfits!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(start = 2.dp, end = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            ClosifyLogo(size = 60.dp)

            NotificationsSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .padding(start = 10.dp)
            )
        }
    }
}

@Composable
private fun NotificationsSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = shape),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "@",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }

                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Buscar usuarios",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    )
}

@Composable
private fun NotificationsCard(
    notifications: List<NotificationUiItem>,
    onOpenUserProfile: (String) -> Unit,
    onOpenPostInProfile: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            notifications.forEachIndexed { index, item ->
                NotificationRow(
                    item = item,
                    onOpenUserProfile = onOpenUserProfile,
                    onOpenPostInProfile = onOpenPostInProfile,
                    onAcceptFriendRequest = onAcceptFriendRequest,
                    onRejectFriendRequest = onRejectFriendRequest
                )
                if (index != notifications.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 58.dp, top = 10.dp, bottom = 10.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    item: NotificationUiItem,
    onOpenUserProfile: (String) -> Unit,
    onOpenPostInProfile: (String) -> Unit,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit
) {
    val notification = item.notification
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(notification.sender.profileImageResId),
            contentDescription = null,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable { onOpenUserProfile(notification.sender.id) },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = notification.postId != null) {
                    notification.postId?.let(onOpenPostInProfile)
                }
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.sender.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { onOpenUserProfile(notification.sender.id) }
                    )
                    Text(
                        text = notification.sender.username,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { onOpenUserProfile(notification.sender.id) }
                    )
                }

                Text(
                    text = notification.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            NotificationMessage(type = notification.type)

            item.post?.let { post ->
                Spacer(modifier = Modifier.height(6.dp))
                GarmentPreview(garments = post.outfit.garments)
            }

            if (notification.type == NotificationType.FRIEND_REQUEST_RECEIVED && item.friendRequest != null) {
                Spacer(modifier = Modifier.height(8.dp))
                FriendRequestActions(
                    requestId = item.friendRequest.id,
                    status = item.friendRequest.status,
                    onAcceptFriendRequest = onAcceptFriendRequest,
                    onRejectFriendRequest = onRejectFriendRequest
                )
            }
        }
    }
}

@Composable
private fun NotificationMessage(type: NotificationType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val icon = when (type) {
            NotificationType.POST_LIKE -> Icons.Outlined.FavoriteBorder
            NotificationType.POST_COMMENT -> Icons.Outlined.ChatBubbleOutline
            NotificationType.FRIEND_REQUEST_RECEIVED,
            NotificationType.FRIEND_REQUEST_ACCEPTED -> Icons.Outlined.Group
        }
        val message = when (type) {
            NotificationType.POST_LIKE -> "Indicó que le gusta tu outfit favorito"
            NotificationType.POST_COMMENT -> "Comentó sobre tu outfit favorito"
            NotificationType.FRIEND_REQUEST_RECEIVED -> "Te envió una solicitud de amistad"
            NotificationType.FRIEND_REQUEST_ACCEPTED -> "Aceptó tu solicitud de amistad"
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GarmentPreview(garments: List<Garment>) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        garments.take(3).forEach { garment ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(garment.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(width = 34.dp, height = 44.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun FriendRequestActions(
    requestId: String,
    status: FriendRequestStatus,
    onAcceptFriendRequest: (String) -> Unit,
    onRejectFriendRequest: (String) -> Unit
) {
    val isPending = status == FriendRequestStatus.PENDING
    val rejectText = if (status == FriendRequestStatus.REJECTED) "Rechazado" else "Rechazar"
    val acceptText = if (status == FriendRequestStatus.ACCEPTED) "Aceptada" else "Aceptar"

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onRejectFriendRequest(requestId) },
            enabled = isPending,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = rejectText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Button(
            onClick = { onAcceptFriendRequest(requestId) },
            enabled = isPending,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = if (status == FriendRequestStatus.ACCEPTED) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                },
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(horizontal = 14.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = acceptText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun NotificationsContentPreview() {
    ClosifyTheme {
        val repository = NotificationRepository.instance
        NotificationsContent(
            uiState = NotificationsUiState(
                notifications = repository.getNotifications().map {
                    NotificationUiItem(
                        notification = it,
                        post = it.postId?.let(repository::getPost)
                    )
                }
            ),
            onOpenUserProfile = {},
            onOpenPostInProfile = {},
            onBackClick = {},
            onAcceptFriendRequest = {},
            onRejectFriendRequest = {}
        )
    }
}
