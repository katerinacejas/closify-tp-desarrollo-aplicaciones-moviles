package com.closify.myapplication.domain.model

import androidx.annotation.DrawableRes

// Perfil publico. Los contadores se calculan desde listas en la capa de estado/UI.
data class UserProfile(
    val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val birthDate: String,
    @param:DrawableRes val bannerImageResId: Int,
    @param:DrawableRes val profileImageResId: Int
)
