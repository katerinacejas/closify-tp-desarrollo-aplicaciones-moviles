package com.closify.myapplication.domain.model

data class OutfitPost(
    val id: String,
    val title: String,
    val type: OutfitPostType,
    val eventDate: String,
    val isLiked: Boolean = false,
    val likedBy: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val garmentImageNames: List<String>
) {
    val likesCount: Int
        get() = likedBy.size

    val commentsCount: Int
        get() = comments.size
}
