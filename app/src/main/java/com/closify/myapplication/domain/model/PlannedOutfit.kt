package com.closify.myapplication.domain.model

data class PlannedOutfit(
    val id: String,
    val user: UserSummary,
    val outfit: Outfit,
    val plannedDate: String,
    val postId: String? = null,
    val createdAt: String
)
