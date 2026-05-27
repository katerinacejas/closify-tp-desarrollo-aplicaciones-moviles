package com.closify.myapplication.domain.model

data class Friendship(
    val id: String,
    val userA: UserSummary,
    val userB: UserSummary,
    val createdAt: String
)
