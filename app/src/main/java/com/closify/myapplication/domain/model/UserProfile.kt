package com.closify.myapplication.domain.model

// Perfil publico. Los contadores se calculan desde listas en la capa de estado/UI.
data class UserProfile(
    val user: User
) {
    val id: String
        get() = user.id

    val name: String
        get() = user.fullName

    val username: String
        get() = user.username

    val bio: String
        get() = user.bio

    val birthDate: String
        get() = user.birthDate

    val bannerImageResId: Int
        get() = user.bannerImageResId

    val profileImageResId: Int
        get() = user.avatarImageResId
}
