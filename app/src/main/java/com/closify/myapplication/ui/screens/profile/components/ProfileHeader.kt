package com.closify.myapplication.ui.screens.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R

@Composable
fun ProfileHeader(
    name: String,
    username: String,
    bio: String,
    birthDate: String,
    friendsCount: Int,
    @DrawableRes bannerImageResId: Int?,
    @DrawableRes profileImageResId: Int?,
    onFriendsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
        ) {
            ProfileBanner(
                bannerImageResId = bannerImageResId,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            ProfileAvatar(
                profileImageResId = profileImageResId,
                modifier = Modifier
                    .padding(start = 22.dp)
                    .align(Alignment.TopStart)
                    .offset(y = 95.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 164.dp, end = 24.dp)
                    .align(Alignment.TopStart)
                    .offset(y = 166.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fecha de nacimiento: $birthDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.clickable(onClick = onFriendsClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Group,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(friendsCount.toString())
                        }
                        append(" amigos")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileBanner(
    @DrawableRes bannerImageResId: Int?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        if (bannerImageResId != null) {
            Image(
                painter = painterResource(id = bannerImageResId),
                contentDescription = "Banner de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            BannerPlaceholder()
        }
    }
}

@Composable
private fun BannerPlaceholder() {
    val logoPositions = listOf(
        DpOffset((-8).dp, 0.dp),
        DpOffset(45.dp, 32.dp),
        DpOffset(90.dp, 0.dp),
        DpOffset(132.dp, 34.dp),
        DpOffset(174.dp, 0.dp),
        DpOffset(218.dp, 32.dp),
        DpOffset(260.dp, 0.dp),
        DpOffset(304.dp, 32.dp),
        DpOffset(348.dp, 0.dp),
        DpOffset(4.dp, 94.dp),
        DpOffset(60.dp, 116.dp),
        DpOffset(108.dp, 92.dp),
        DpOffset(154.dp, 116.dp),
        DpOffset(200.dp, 92.dp),
        DpOffset(248.dp, 116.dp),
        DpOffset(296.dp, 92.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .clipToBounds()
    ) {
        logoPositions.forEachIndexed { index, offset ->
            Image(
                painter = painterResource(id = R.drawable.ic_closify_logo),
                contentDescription = null,
                modifier = Modifier
                    .offset(offset.x, offset.y)
                    .size(30.dp)
                    .alpha(0.18f)
                    .graphicsLayer(rotationZ = if (index % 2 == 0) -12f else 14f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    @DrawableRes profileImageResId: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(126.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (profileImageResId != null) {
            Image(
                painter = painterResource(id = profileImageResId),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            ProfileAvatarPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileAvatarPlaceholder(
    modifier: Modifier = Modifier
) {
    val avatarColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
        val centerX = size.width / 2f

        drawCircle(
            color = avatarColor,
            radius = 16.dp.toPx(),
            center = Offset(centerX, 28.dp.toPx()),
            style = stroke
        )

        drawArc(
            color = avatarColor,
            startAngle = 202f,
            sweepAngle = 136f,
            useCenter = false,
            topLeft = Offset(8.dp.toPx(), 62.dp.toPx()),
            size = Size(size.width - 16.dp.toPx(), 78.dp.toPx()),
            style = stroke
        )
    }
}
