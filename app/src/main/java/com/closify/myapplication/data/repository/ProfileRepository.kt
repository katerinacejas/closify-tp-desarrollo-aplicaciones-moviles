package com.closify.myapplication.data.repository

import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.domain.model.UserProfile

class ProfileRepository {

    companion object {
        val instance = ProfileRepository()
    }

    fun getProfile(): UserProfile = UserProfile(
        id = "user_1",
        name = "Katerina Cejas",
        username = "@kate_cejas_1999",
        bio = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
        birthDate = "3 de septiembre de 1999",
        friendsCount = 12,
        garmentsCount = 23,
        wardrobeUsagePercentage = 70,
        favoriteOutfitsCount = 2,
        plannedOutfitsCount = 2,
        bannerImageResId = R.drawable.banner_default,
        profileImageResId = R.drawable.avatar_default
    )

    fun getFriends(): List<UserSummary> = listOf(
        UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default),
        UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default),
        UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default),
        UserSummary("4", "Ayelen Balmaceda", "@ayee_balmaceda_1", R.drawable.avatar_default),
        UserSummary("5", "Camila Martinez", "@camii_martinez", R.drawable.avatar_default)
    )

    fun getPosts(): List<OutfitPost> = listOf(
        OutfitPost(
            id = "1",
            title = "Mi outfit para mi cumpleanos! <3",
            type = OutfitPostType.FAVORITE,
            eventDate = "25 de mayo de 2026",
            isLiked = false,
            likedBy = listOf(
                Like("1", UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default), "25 de mayo de 2026"),
                Like("2", UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default), "25 de mayo de 2026"),
                Like("3", UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default), "25 de mayo de 2026")
            ),
            comments = listOf(
                Comment(
                    id = "1",
                    user = UserSummary("6", "Andrea Gonzalez", "@andrea_gonzalez", R.drawable.avatar_default),
                    text = "Ay que lindo outfit amigaaa! Me encanta <3",
                    createdAt = "25 de mayo de 2026"
                ),
                Comment(
                    id = "2",
                    user = UserSummary("7", "Agustina Marrapodia", "@agustina_marrapodia", R.drawable.avatar_default),
                    text = "Amigaaaa, te queda hermoso, tenes que prestarme esa blusa :)",
                    createdAt = "25 de mayo de 2026"
                )
            ),
            garmentImageNames = listOf("blusa_1", "jean_1", "zapatillas_blancas")
        ),
        OutfitPost(
            id = "2",
            title = "El outfit que me voy a poner en el cumple de mi novio <3",
            type = OutfitPostType.PLANNED,
            eventDate = "25 de diciembre de 2026",
            isLiked = false,
            likedBy = emptyList(),
            comments = emptyList(),
            garmentImageNames = listOf("vestido_floral", "botas_negras")
        )
    )
}
