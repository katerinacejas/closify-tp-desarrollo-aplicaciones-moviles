package com.closify.myapplication.ui.screens.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.ui.components.OutfitPostCard

@Composable
fun ProfileOutfitCard(
    outfit: OutfitPost,
    isLikedByCurrentUser: Boolean,
    onLikeClick: () -> Unit,
    onLikesTextClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onCommentsTextClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutfitPostCard(
        post = outfit,
        isLikedByCurrentUser = isLikedByCurrentUser,
        onLikeClick = onLikeClick,
        onLikesTextClick = onLikesTextClick,
        onCommentsClick = onCommentsClick,
        onCommentsTextClick = onCommentsTextClick,
        onEditClick = onEditClick,
        modifier = modifier
    )
}
