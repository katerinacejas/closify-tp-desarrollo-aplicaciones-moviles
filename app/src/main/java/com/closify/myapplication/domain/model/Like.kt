package com.closify.myapplication.domain.model

data class Like(
    val id: String,
    val user: UserSummary,
    val createdAt: String
)
