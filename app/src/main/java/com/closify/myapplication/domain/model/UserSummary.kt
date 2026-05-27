package com.closify.myapplication.domain.model

import androidx.annotation.DrawableRes

// Usuario reducido para likes, comentarios, amigos y notificaciones.
// El id permite navegar al perfil completo sin cargar toda la entidad User.
data class UserSummary(
    val id: String,
    val name: String,
    val username: String,
    @param:DrawableRes val profileImageResId: Int
)
