package com.closify.myapplication.domain.model

import androidx.annotation.DrawableRes

// es distinto a la entidad User que haya despues con su email y contraseña, este es el user que es perfil publico
data class UserProfile(
    val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val birthDate: String,
    val friendsCount: Int,
    val garmentsCount: Int,
    val wardrobeUsagePercentage: Int,
    val favoriteOutfitsCount: Int,
    val plannedOutfitsCount: Int,
    @param:DrawableRes val bannerImageResId: Int,
    @param:DrawableRes val profileImageResId: Int
)
