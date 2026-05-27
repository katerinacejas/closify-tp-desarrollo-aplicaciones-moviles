package com.closify.myapplication.domain.model

data class Comment(
    val id: String,
    val user: UserSummary,
    val text: String,
    val createdAt: String
)
