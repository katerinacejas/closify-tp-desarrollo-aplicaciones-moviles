package com.closify.myapplication.domain.model

data class OutfitPost(
    val id: String,
    val author: UserSummary,
    val outfit: Outfit,
    val title: String?,
    val type: OutfitPostType,
    val createdAt: String,
    val plannedDate: String? = null,
    val likedBy: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList()
) {
    val likesCount: Int
        get() = likedBy.size

    val commentsCount: Int
        get() = comments.size
}
