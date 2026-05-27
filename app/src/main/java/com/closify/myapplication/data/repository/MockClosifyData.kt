package com.closify.myapplication.data.repository

import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.FriendRequest
import com.closify.myapplication.domain.model.FriendRequestStatus
import com.closify.myapplication.domain.model.Friendship
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.Notification
import com.closify.myapplication.domain.model.NotificationType
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.SuggestedOutfit
import com.closify.myapplication.domain.model.User
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.domain.model.WeatherCondition

internal object MockClosifyData {
    const val CURRENT_USER_ID = "user_1"
    private const val RESOURCE_PREFIX = "android.resource://com.closify.myapplication/drawable/"

    val currentUser = User(
        id = CURRENT_USER_ID,
        fullName = "Katerina Cejas",
        username = "@kate_cejas_1999",
        email = "kate@closify.com",
        birthDate = "3 de septiembre de 1999",
        bio = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
        avatarImageResId = R.drawable.avatar_default,
        bannerImageResId = R.drawable.banner_default,
        createdAt = "1 de mayo de 2026"
    )

    val users = listOf(
        currentUser,
        user("user_2", "Ayelen Martinez", "@aye_martinez"),
        user("user_3", "Milagros Fava", "@miliifava"),
        user("user_4", "Ailen Garcia", "@ailu_garcia"),
        user("user_5", "Ayelen Balmaceda", "@ayee_balmaceda_1"),
        user("user_6", "Camila Martinez", "@camii_martinez"),
        user("user_7", "Andrea Gonzalez", "@andrea_gonzalez"),
        user("user_8", "Agustina Marrapodia", "@agustina_marrapodia")
    )

    val friends = users
        .filter { it.id in setOf("user_2", "user_3", "user_4", "user_5", "user_6") }
        .map { it.toSummary() }

    val friendships = friends.mapIndexed { index, friend ->
        Friendship(
            id = "friendship_${index + 1}",
            userA = currentUser.toSummary(),
            userB = friend,
            createdAt = "10 de mayo de 2026"
        )
    }

    val friendRequests = listOf(
        FriendRequest(
            id = "request_1",
            sender = summary("user_7"),
            receiver = currentUser.toSummary(),
            status = FriendRequestStatus.PENDING,
            createdAt = "26 de mayo de 2026"
        ),
        FriendRequest(
            id = "request_2",
            sender = currentUser.toSummary(),
            receiver = summary("user_8"),
            status = FriendRequestStatus.ACCEPTED,
            createdAt = "20 de mayo de 2026",
            respondedAt = "21 de mayo de 2026"
        )
    )

    val garments = listOf(
        garment("blusa_1", "Blusa rosa", GarmentCategory.TOP),
        garment("jean_1", "Jean claro", GarmentCategory.BOTTOM),
        garment("zapatillas_blancas", "Zapatillas blancas", GarmentCategory.FOOTWEAR),
        garment("vestido_floral", "Vestido floral", GarmentCategory.FULL_BODY),
        garment("botas_negras", "Botas negras", GarmentCategory.FOOTWEAR),
        garment("camisa_azul", "Camisa azul", GarmentCategory.TOP),
        garment("buzo_gris", "Buzo gris", GarmentCategory.TOP),
        garment("pantalon_beige", "Pantalon beige", GarmentCategory.BOTTOM),
        garment("pantalon_elegante", "Pantalon elegante", GarmentCategory.BOTTOM),
        garment("blusa_elegante_1", "Blusa elegante", GarmentCategory.TOP),
        garment("zapatillas_negras", "Zapatillas negras", GarmentCategory.FOOTWEAR),
        garment("campera_jean", "Campera de jean", GarmentCategory.OUTERWEAR),
        garment("falda_elegante", "Falda elegante", GarmentCategory.BOTTOM),
        garment("zapatos_elegantes_1", "Zapatos elegantes", GarmentCategory.FOOTWEAR),
        garment("vestido_negro", "Vestido negro", GarmentCategory.FULL_BODY)
    )

    val outfits = listOf(
        outfit("outfit_1", "Mi outfit para mi cumpleanos", "25 de mayo de 2026", "blusa_1", "jean_1", "zapatillas_blancas"),
        outfit("outfit_2", "Cumple de mi novio", "25 de mayo de 2026", "vestido_floral", "botas_negras"),
        outfit("outfit_3", null, "20 de mayo de 2026", "camisa_azul", "jean_1", "zapatillas_blancas"),
        outfit("outfit_4", "Cursada tranquila", "26 de mayo de 2026", "buzo_gris", "pantalon_beige", "zapatillas_blancas"),
        outfit("outfit_5", "Salida a merendar", "18 de mayo de 2026", "blusa_1", "pantalon_elegante", "botas_negras")
    )

    val outfitPosts = listOf(
        OutfitPost(
            id = "post_1",
            author = currentUser.toSummary(),
            outfit = outfit("outfit_1"),
            title = "Mi outfit para mi cumpleanos! <3",
            type = OutfitPostType.FAVORITE,
            createdAt = "25 de mayo de 2026",
            likedBy = listOf(
                Like("like_1", summary("user_4"), "25 de mayo de 2026"),
                Like("like_2", summary("user_3"), "25 de mayo de 2026"),
                Like("like_3", summary("user_2"), "25 de mayo de 2026")
            ),
            comments = listOf(
                Comment("comment_1", summary("user_7"), "Ay que lindo outfit amigaaa! Me encanta <3", "25 de mayo de 2026"),
                Comment("comment_2", summary("user_8"), "Amigaaaa, te queda hermoso, tenes que prestarme esa blusa :)", "25 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_2",
            author = currentUser.toSummary(),
            outfit = outfit("outfit_2"),
            title = "El outfit que me voy a poner en el cumple de mi novio <3",
            type = OutfitPostType.PLANNED,
            createdAt = "25 de mayo de 2026",
            plannedDate = "25 de diciembre de 2026"
        ),
        OutfitPost(
            id = "post_3",
            author = currentUser.toSummary(),
            outfit = outfit("outfit_3"),
            title = null,
            type = OutfitPostType.FAVORITE,
            createdAt = "20 de mayo de 2026"
        ),
        OutfitPost(
            id = "post_4",
            author = currentUser.toSummary(),
            outfit = outfit("outfit_4"),
            title = "Outfit para cursar tranquila",
            type = OutfitPostType.PLANNED,
            createdAt = "26 de mayo de 2026",
            plannedDate = "10 de junio de 2026",
            likedBy = listOf(Like("like_4", summary("user_6"), "26 de mayo de 2026")),
            comments = listOf(Comment("comment_4", summary("user_4"), "Comodo y lindo, re va!", "26 de mayo de 2026"))
        ),
        OutfitPost(
            id = "post_5",
            author = currentUser.toSummary(),
            outfit = outfit("outfit_5"),
            title = "Favorito para salir a merendar",
            type = OutfitPostType.FAVORITE,
            createdAt = "18 de mayo de 2026",
            likedBy = listOf(
                Like("like_5", summary("user_2"), "19 de mayo de 2026"),
                Like("like_6", summary("user_3"), "19 de mayo de 2026")
            )
        )
    )

    val suggestedOutfits = listOf(
        SuggestedOutfit(
            id = "suggested_1",
            garments = outfit("outfit_3").garments,
            climate = WeatherCondition.MILD,
            occasion = Occasion.ACADEMIC,
            generatedAt = "27 de mayo de 2026"
        ),
        SuggestedOutfit(
            id = "suggested_2",
            garments = outfit("outfit_5").garments,
            climate = WeatherCondition.MILD,
            occasion = Occasion.CASUAL,
            generatedAt = "27 de mayo de 2026"
        )
    )

    val notifications = listOf(
        Notification(
            id = "notification_1",
            receiver = currentUser.toSummary(),
            sender = summary("user_4"),
            type = NotificationType.POST_LIKE,
            postId = "post_1",
            createdAt = "25 de mayo de 2026"
        ),
        Notification(
            id = "notification_2",
            receiver = currentUser.toSummary(),
            sender = summary("user_7"),
            type = NotificationType.POST_COMMENT,
            postId = "post_1",
            commentId = "comment_1",
            createdAt = "25 de mayo de 2026"
        ),
        Notification(
            id = "notification_3",
            receiver = currentUser.toSummary(),
            sender = summary("user_7"),
            type = NotificationType.FRIEND_REQUEST_RECEIVED,
            friendRequestId = "request_1",
            createdAt = "26 de mayo de 2026"
        ),
        Notification(
            id = "notification_4",
            receiver = currentUser.toSummary(),
            sender = summary("user_8"),
            type = NotificationType.FRIEND_REQUEST_ACCEPTED,
            friendRequestId = "request_2",
            createdAt = "21 de mayo de 2026",
            read = true
        )
    )

    fun userById(userId: String): User? = users.firstOrNull { it.id == userId }

    fun summary(userId: String): UserSummary =
        requireNotNull(userById(userId)) { "Unknown mock user id: $userId" }.toSummary()

    fun outfit(outfitId: String): Outfit =
        requireNotNull(outfits.firstOrNull { it.id == outfitId }) { "Unknown mock outfit id: $outfitId" }

    private fun user(id: String, fullName: String, username: String): User = User(
        id = id,
        fullName = fullName,
        username = username,
        birthDate = "",
        bio = "",
        avatarImageResId = R.drawable.avatar_default,
        bannerImageResId = R.drawable.banner_default,
        createdAt = "1 de mayo de 2026"
    )

    private fun garment(
        id: String,
        name: String,
        category: GarmentCategory,
    ): Garment = Garment(
        id = id,
        ownerUserId = CURRENT_USER_ID,
        name = name,
        category = category,
        imageUrl = "$RESOURCE_PREFIX$id",
        suitableWeather = setOf(WeatherCondition.ANY),
        suitableOccasions = setOf(Occasion.ANY),
        createdAt = "1 de mayo de 2026"
    )

    private fun outfit(
        id: String,
        name: String?,
        createdAt: String,
        vararg garmentIds: String
    ): Outfit {
        val garmentsById = garments.associateBy { it.id }
        return Outfit(
            id = id,
            garments = garmentIds.mapNotNull { garmentsById[it] },
            ownerUserId = CURRENT_USER_ID,
            name = name,
            createdAt = createdAt
        )
    }
}
