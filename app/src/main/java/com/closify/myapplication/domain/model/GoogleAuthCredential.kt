package com.closify.myapplication.domain.model

data class GoogleAuthCredential(
    val idToken: String,
    val email: String?,
    val displayName: String?,
    val profileImageUrl: String?
)
