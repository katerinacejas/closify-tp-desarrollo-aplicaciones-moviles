package com.closify.myapplication.data.repository

import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.GarmentColor
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserProfile
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.domain.model.WeatherCondition

class ProfileRepository {

    companion object {
        val instance = ProfileRepository()
    }

    private val currentUser = UserSummary(
        id = "user_1",
        name = "Katerina Cejas",
        username = "@kate_cejas_1999",
        profileImageResId = R.drawable.avatar_default
    )

    fun getProfile(): UserProfile = UserProfile(
        id = currentUser.id,
        name = currentUser.name,
        username = currentUser.username,
        bio = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
        birthDate = "3 de septiembre de 1999",
        bannerImageResId = R.drawable.banner_default,
        profileImageResId = currentUser.profileImageResId
    )

    fun getFriends(): List<UserSummary> = listOf(
        UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default),
        UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default),
        UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default),
        UserSummary("4", "Ayelen Balmaceda", "@ayee_balmaceda_1", R.drawable.avatar_default),
        UserSummary("5", "Camila Martinez", "@camii_martinez", R.drawable.avatar_default)
    )

    fun getWardrobeGarments(): List<Garment> = listOf(
        garment("blusa_1", "Blusa rosa", GarmentCategory.TOP, GarmentColor.PINK),
        garment("jean_1", "Jean claro", GarmentCategory.BOTTOM, GarmentColor.LIGHT_BLUE),
        garment("zapatillas_blancas", "Zapatillas blancas", GarmentCategory.FOOTWEAR, GarmentColor.WHITE),
        garment("vestido_floral", "Vestido floral", GarmentCategory.FULL_BODY, GarmentColor.PRINT),
        garment("botas_negras", "Botas negras", GarmentCategory.FOOTWEAR, GarmentColor.BLACK),
        garment("camisa_azul", "Camisa azul", GarmentCategory.TOP, GarmentColor.BLUE),
        garment("buzo_gris", "Buzo gris", GarmentCategory.TOP, GarmentColor.GRAY),
        garment("pantalon_beige", "Pantalon beige", GarmentCategory.BOTTOM, GarmentColor.BEIGE),
        garment("pantalon_elegante", "Pantalon elegante", GarmentCategory.BOTTOM, GarmentColor.BLACK),
        garment("blusa_elegante_1", "Blusa elegante", GarmentCategory.TOP, GarmentColor.BURGUNDY),
        garment("zapatillas_negras", "Zapatillas negras", GarmentCategory.FOOTWEAR, GarmentColor.BLACK),
        garment("campera_jean", "Campera de jean", GarmentCategory.OUTERWEAR, GarmentColor.LIGHT_BLUE),
        garment("falda_elegante", "Falda elegante", GarmentCategory.BOTTOM, GarmentColor.BLACK),
        garment("zapatos_elegantes_1", "Zapatos elegantes", GarmentCategory.FOOTWEAR, GarmentColor.BEIGE),
        garment("vestido_negro", "Vestido negro", GarmentCategory.FULL_BODY, GarmentColor.BLACK)
    )

    fun getWardrobeUsagePercentage(): Int = 70

    fun getPosts(): List<OutfitPost> {
        val wardrobe = getWardrobeGarments().associateBy { it.id }

        fun outfit(id: String, vararg garmentIds: String): Outfit = Outfit(
            id = id,
            garments = garmentIds.mapNotNull { wardrobe[it] }
        )

        return listOf(
            OutfitPost(
                id = "1",
                author = currentUser,
                outfit = outfit("outfit_1", "blusa_1", "jean_1", "zapatillas_blancas"),
                title = "Mi outfit para mi cumpleanos! <3",
                type = OutfitPostType.FAVORITE,
                createdAt = "25 de mayo de 2026",
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
                )
            ),
            OutfitPost(
                id = "2",
                author = currentUser,
                outfit = outfit("outfit_2", "vestido_floral", "botas_negras"),
                title = "El outfit que me voy a poner en el cumple de mi novio <3",
                type = OutfitPostType.PLANNED,
                createdAt = "25 de mayo de 2026",
                plannedDate = "25 de diciembre de 2026",
                likedBy = emptyList(),
                comments = emptyList()
            ),
            OutfitPost(
                id = "3",
                author = currentUser,
                outfit = outfit("outfit_3", "camisa_azul", "jean_1", "zapatillas_blancas"),
                title = null,
                type = OutfitPostType.FAVORITE,
                createdAt = "20 de mayo de 2026",
                likedBy = emptyList(),
                comments = emptyList()
            ),
            OutfitPost(
                id = "4",
                author = currentUser,
                outfit = outfit("outfit_4", "buzo_gris", "pantalon_beige", "zapatillas_blancas"),
                title = "Outfit para cursar tranquila",
                type = OutfitPostType.PLANNED,
                createdAt = "26 de mayo de 2026",
                plannedDate = "10 de junio de 2026",
                likedBy = listOf(
                    Like("4", UserSummary("5", "Camila Martinez", "@camii_martinez", R.drawable.avatar_default), "26 de mayo de 2026")
                ),
                comments = listOf(
                    Comment(
                        id = "4",
                        user = UserSummary("3", "Ailen Garcia", "@ailu_garcia", R.drawable.avatar_default),
                        text = "Comodo y lindo, re va!",
                        createdAt = "26 de mayo de 2026"
                    )
                )
            ),
            OutfitPost(
                id = "5",
                author = currentUser,
                outfit = outfit("outfit_5", "blusa_1", "pantalon_elegante", "botas_negras"),
                title = "Favorito para salir a merendar",
                type = OutfitPostType.FAVORITE,
                createdAt = "18 de mayo de 2026",
                likedBy = listOf(
                    Like("5", UserSummary("1", "Ayelen Martinez", "@aye_martinez", R.drawable.avatar_default), "19 de mayo de 2026"),
                    Like("6", UserSummary("2", "Milagros Fava", "@miliifava", R.drawable.avatar_default), "19 de mayo de 2026")
                ),
                comments = emptyList()
            )
        )
    }

    private fun garment(
        id: String,
        name: String,
        category: GarmentCategory,
        color: GarmentColor
    ): Garment = Garment(
        id = id,
        name = name,
        category = category,
        color = color,
        imageUrl = id,
        suitableWeather = setOf(WeatherCondition.ANY),
        suitableOccasions = setOf(Occasion.ANY)
    )
}
