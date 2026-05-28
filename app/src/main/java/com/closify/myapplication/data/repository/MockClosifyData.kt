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
import com.closify.myapplication.domain.model.UserProfile
import com.closify.myapplication.domain.model.UserSummary
import com.closify.myapplication.domain.model.WeatherCondition

internal object MockClosifyData {
    data class MockAuthUser(
        val user: User,
        val password: String
    )

    const val MARIA_USER_ID = "user_1"
    const val JUAN_USER_ID = "user_17"
    const val CURRENT_USER_ID = MARIA_USER_ID
    const val CURRENT_DATE_LABEL = "27 de mayo de 2026"
    const val PUBLIC_PROFILE_BASE_GARMENTS_COUNT = 24
    const val DEFAULT_USER_BIRTH_DATE = "3 de septiembre de 1999"
    const val DEFAULT_USER_BIO = "Organizo mi guardarropa para no decir \"no tengo nada que ponerme\" todos los dias"
    val currentWeather = WeatherCondition.MILD
    private const val RESOURCE_PREFIX = "android.resource://com.closify.myapplication/drawable/"
    private val defaultFriendIdsByUser = mapOf(
        MARIA_USER_ID to setOf(
            "user_2",
            "user_4",
            "user_5",
            "user_6",
            "user_9",
            "user_10",
            JUAN_USER_ID
        ),
        JUAN_USER_ID to setOf(
            MARIA_USER_ID,
            "user_2",
            "user_6",
            "user_11"
        )
    )
    private val friendIdsByUser = buildFriendIdsByUser()

    val currentUser = User(
        id = CURRENT_USER_ID,
        email = "maria@gmail.com",
        profile = UserProfile(
            id = CURRENT_USER_ID,
            fullName = "Maria Cejas",
            username = "@maria_cejas",
            birthDate = "3 de septiembre de 1999",
            bio = "hola soy maria, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
            avatarImageResId = R.drawable.avatar_default,
            bannerImageResId = R.drawable.banner_default
        ),
        createdAt = "1 de mayo de 2026"
    )

    private val juanUser = User(
        id = JUAN_USER_ID,
        email = "juan@gmail.com",
        profile = UserProfile(
            id = JUAN_USER_ID,
            fullName = "Juan Perez",
            username = "@juan_perez",
            birthDate = "18 de octubre de 1998",
            bio = "Me gusta armar outfits simples para cursar, salir y no repetir siempre lo mismo.",
            avatarImageResId = R.drawable.avatar_default,
            bannerImageResId = R.drawable.banner_default
        ),
        createdAt = "2 de mayo de 2026"
    )

    private val authUsers = mutableListOf(
        MockAuthUser(currentUser, "Maria123!"),
        MockAuthUser(juanUser, "Juan123!")
    )

    val users = mutableListOf(
        currentUser,
        juanUser,
        user(
            "user_2",
            "Ayelen Martinez",
            "@aye_martinez",
            bio = "Organizo mi guardarropa para no decir \"no tengo nada que ponerme\" todos los dias",
            birthDate = "3 de septiembre de 1999"
        ),
        user("user_3", "Milagros Fava", "@miliifava"),
        user("user_4", "Ailen Garcia", "@ailu_garcia"),
        user("user_5", "Ayelen Balmaceda", "@ayee_balmaceda_1"),
        user("user_6", "Camila Martinez", "@camii_martinez"),
        user("user_7", "Andrea Gonzalez", "@andrea_gonzalez"),
        user("user_8", "Agustina Marrapodia", "@agustina_marrapodia"),
        user("user_9", "Daiana Krembs", "@dai_krembs_"),
        user("user_10", "Abril Sejas", "@abrilcini_16"),
        user("user_11", "Ayelen Perez", "@ayeperezzz", bio = "Me gusta armar looks con prendas simples."),
        user("user_12", "Ayito", "@ayitoo_333", bio = "Fan de los outfits comodos."),
        user("user_13", "Aylin Mendez", "@aylin_", bio = "Siempre guardo ideas para salir."),
        user("user_14", "Ayla Lopez", "@aylalo", bio = "Ropa, agenda y cafe."),
        user("user_15", "Aylen nnnn", "@aylllen", bio = "Probando combinaciones nuevas."),
        user("user_16", "Ayelen Perezz", "@ayeperezz", bio = "Looks casuales para todos los dias.")
    )

    val friends: List<UserSummary>
        get() = users
        .filter { it.id in friendIds(CURRENT_USER_ID) }
        .map { it.toSummary() }

    val friendships: List<Friendship>
        get() = friendIdsByUser
            .flatMap { (userId, friendIds) ->
                friendIds.map { friendId -> setOf(userId, friendId) }
            }
            .distinct()
            .mapIndexedNotNull { index, pair ->
                val ids = pair.toList()
                val userA = userById(ids[0])?.toSummary()
                val userB = userById(ids[1])?.toSummary()
                if (userA == null || userB == null) {
                    null
                } else {
                    Friendship(
                        id = "friendship_${index + 1}",
                        userA = userA,
                        userB = userB,
                        createdAt = "10 de mayo de 2026"
                    )
                }
            }

    private val mutableFriendRequests = defaultFriendRequests().toMutableList()

    val friendRequests: List<FriendRequest>
        get() = mutableFriendRequests.toList()

    private fun defaultFriendRequests(): List<FriendRequest> = listOf(
        FriendRequest(
            id = "request_1",
            sender = summary("user_3"),
            receiver = currentUser.toSummary(),
            status = FriendRequestStatus.PENDING,
            createdAt = "hace 3 horas"
        ),
        FriendRequest(
            id = "request_2",
            sender = summary("user_6"),
            receiver = currentUser.toSummary(),
            status = FriendRequestStatus.ACCEPTED,
            createdAt = "hace 57 minutos",
            respondedAt = "hace 57 minutos"
        ),
        FriendRequest(
            id = "request_3",
            sender = currentUser.toSummary(),
            receiver = summary("user_4"),
            status = FriendRequestStatus.ACCEPTED,
            createdAt = "hace 5 horas",
            respondedAt = "hace 5 horas"
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
        garment("vestido_negro", "Vestido negro", GarmentCategory.FULL_BODY),
        garment("juan_camisa_azul", "Camisa azul de Juan", GarmentCategory.TOP, ownerUserId = JUAN_USER_ID, imageName = "camisa_azul"),
        garment("juan_pantalon_beige", "Pantalon beige de Juan", GarmentCategory.BOTTOM, ownerUserId = JUAN_USER_ID, imageName = "pantalon_beige"),
        garment("juan_zapatillas", "Zapatillas blancas de Juan", GarmentCategory.FOOTWEAR, ownerUserId = JUAN_USER_ID, imageName = "zapatillas_blancas"),
        garment("juan_buzo_gris", "Buzo gris de Juan", GarmentCategory.TOP, ownerUserId = JUAN_USER_ID, imageName = "buzo_gris"),
        garment("juan_jean", "Jean de Juan", GarmentCategory.BOTTOM, ownerUserId = JUAN_USER_ID, imageName = "jean_1")
    )

    val outfits = listOf(
        outfit("outfit_1", "Mi outfit para mi cumpleanos", "25 de mayo de 2026", "blusa_1", "jean_1", "zapatillas_blancas"),
        outfit("outfit_2", "Cumple de mi novio", "25 de mayo de 2026", "vestido_floral", "botas_negras"),
        outfit("outfit_3", null, "20 de mayo de 2026", "camisa_azul", "jean_1", "zapatillas_blancas"),
        outfit("outfit_4", "Cursada tranquila", "26 de mayo de 2026", "buzo_gris", "pantalon_beige", "zapatillas_blancas"),
        outfit("outfit_5", "Salida a merendar", "18 de mayo de 2026", "blusa_1", "pantalon_elegante", "botas_negras"),
        outfit("outfit_6", "Civil de mi hermano", "15 de mayo de 2026", "blusa_1", "pantalon_beige", "zapatos_elegantes_1"),
        outfit("outfit_7", "Bautismo de mi ahijado", "5 de mayo de 2026", "blusa_elegante_1", "falda_elegante", "zapatos_elegantes_1"),
        outfit("outfit_8", "Ready para la facu", "15 de abril de 2026", "camisa_azul", "jean_1", "zapatillas_blancas"),
        outfit("outfit_9", "Parcial aprobado", "27 de mayo de 2026", "camisa_azul", "pantalon_elegante", "zapatillas_blancas"),
        outfit("outfit_10", "Domingo tranqui", "23 de mayo de 2026", "buzo_gris", "jean_1", "zapatillas_negras"),
        outfit("outfit_11", "Cena con amigas", "21 de mayo de 2026", "blusa_elegante_1", "pantalon_elegante", "botas_negras"),
        outfit("outfit_12", "Look para lluvia", "12 de mayo de 2026", "campera_jean", "pantalon_beige", "zapatillas_negras"),
        outfit("outfit_13", "Cena de fin de ano", "23 de mayo de 2026", "blusa_elegante_1", "falda_elegante", "zapatos_elegantes_1"),
        outfit("outfit_14", "Primer post de Ayelen", "28 de mayo de 2026", "blusa_1", "pantalon_elegante", "zapatos_elegantes_1"),
        outfit("outfit_15", "Look comodo de Ayito", "22 de mayo de 2026", "buzo_gris", "jean_1", "zapatillas_blancas"),
        outfit("outfit_16", "Plan de Aylin", "17 de mayo de 2026", "camisa_azul", "pantalon_beige", "botas_negras"),
        ownedOutfit("outfit_17", "Presentacion en la facu", "27 de mayo de 2026", JUAN_USER_ID, "juan_camisa_azul", "juan_pantalon_beige", "juan_zapatillas"),
        ownedOutfit("outfit_18", "Domingo relajado", "24 de mayo de 2026", JUAN_USER_ID, "juan_buzo_gris", "juan_jean", "juan_zapatillas")
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
        ),
        OutfitPost(
            id = "post_6",
            author = summary("user_6"),
            outfit = outfit("outfit_6"),
            title = "Outfit para el civil de mi hermano",
            type = OutfitPostType.FAVORITE,
            createdAt = "15 de mayo de 2026",
            likedBy = listOf(
                Like("like_7", summary("user_2"), "15 de mayo de 2026"),
                Like("like_8", summary("user_3"), "15 de mayo de 2026"),
                Like("like_9", summary("user_4"), "15 de mayo de 2026"),
                Like("like_10", summary("user_5"), "15 de mayo de 2026")
            ),
            comments = listOf(
                Comment("comment_5", summary("user_7"), "Ay que lindo outfit amigaaa! Me encanta <3", "15 de mayo de 2026"),
                Comment("comment_6", summary("user_8"), "Amigaaaa, te queda hermoso, tenes que prestarme esa blusa :)", "15 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_7",
            author = summary("user_9"),
            outfit = outfit("outfit_7"),
            title = "Esto me puse para el bautismo de mi ahijado <3",
            type = OutfitPostType.PLANNED,
            createdAt = "5 de mayo de 2026",
            plannedDate = "5 de mayo de 2026",
            likedBy = listOf(
                Like("like_11", summary("user_3"), "5 de mayo de 2026"),
                Like("like_12", summary("user_5"), "5 de mayo de 2026"),
                Like("like_13", summary("user_6"), "5 de mayo de 2026")
            ),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_8",
            author = summary("user_10"),
            outfit = outfit("outfit_8"),
            title = "El resultado de mi get ready with me para ir a la facu",
            type = OutfitPostType.FAVORITE,
            createdAt = "15 de abril de 2026",
            likedBy = emptyList(),
            comments = listOf(
                Comment("comment_7", summary("user_2"), "Re lindo para cursar!", "15 de abril de 2026")
            )
        ),
        OutfitPost(
            id = "post_9",
            author = summary("user_2"),
            outfit = outfit("outfit_9"),
            title = "Outfit para rendir y salir festejando",
            type = OutfitPostType.FAVORITE,
            createdAt = "27 de mayo de 2026",
            likedBy = listOf(
                Like("like_14", summary("user_6"), "27 de mayo de 2026"),
                Like("like_15", summary("user_10"), "27 de mayo de 2026")
            ),
            comments = listOf(
                Comment("comment_8", summary("user_5"), "Ese pantalon queda demasiado bien!", "27 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_10",
            author = summary("user_3"),
            outfit = outfit("outfit_10"),
            title = "Domingo tranqui pero linda igual",
            type = OutfitPostType.FAVORITE,
            createdAt = "23 de mayo de 2026",
            likedBy = listOf(
                Like("like_16", summary("user_2"), "23 de mayo de 2026")
            ),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_11",
            author = summary("user_4"),
            outfit = outfit("outfit_11"),
            title = "Cena con amigas despues del trabajo",
            type = OutfitPostType.PLANNED,
            createdAt = "21 de mayo de 2026",
            plannedDate = "30 de mayo de 2026",
            likedBy = emptyList(),
            comments = listOf(
                Comment("comment_9", summary("user_3"), "Lookazo total", "21 de mayo de 2026"),
                Comment("comment_10", summary("user_9"), "Necesito esas botas", "21 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_12",
            author = summary("user_5"),
            outfit = outfit("outfit_12"),
            title = "Probando capas para dias raros",
            type = OutfitPostType.FAVORITE,
            createdAt = "12 de mayo de 2026",
            likedBy = listOf(
                Like("like_17", summary("user_10"), "12 de mayo de 2026"),
                Like("like_18", summary("user_6"), "12 de mayo de 2026"),
                Like("like_19", summary("user_2"), "12 de mayo de 2026")
            ),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_13",
            author = summary("user_2"),
            outfit = outfit("outfit_13"),
            title = "Asi me vesti para la cena de fin de ano",
            type = OutfitPostType.FAVORITE,
            createdAt = "23 de mayo de 2026",
            likedBy = listOf(
                Like("like_20", summary("user_3"), "23 de mayo de 2026"),
                Like("like_21", summary("user_10"), "23 de mayo de 2026")
            ),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_14",
            author = summary("user_11"),
            outfit = outfit("outfit_14"),
            title = "Mi primer outfit favorito en Closify",
            type = OutfitPostType.FAVORITE,
            createdAt = "28 de mayo de 2026",
            likedBy = listOf(
                Like("like_22", summary("user_12"), "28 de mayo de 2026")
            ),
            comments = listOf(
                Comment("comment_11", summary("user_13"), "Ese look quedo re prolijo", "28 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_15",
            author = summary("user_12"),
            outfit = outfit("outfit_15"),
            title = "Comodo para hacer mil tramites",
            type = OutfitPostType.FAVORITE,
            createdAt = "22 de mayo de 2026",
            likedBy = emptyList(),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_16",
            author = summary("user_13"),
            outfit = outfit("outfit_16"),
            title = "Planificado para salir despues de cursar",
            type = OutfitPostType.PLANNED,
            createdAt = "17 de mayo de 2026",
            plannedDate = "4 de junio de 2026",
            likedBy = listOf(
                Like("like_23", summary("user_11"), "17 de mayo de 2026"),
                Like("like_24", summary("user_12"), "17 de mayo de 2026")
            ),
            comments = emptyList()
        ),
        OutfitPost(
            id = "post_17",
            author = summary(JUAN_USER_ID),
            outfit = outfit("outfit_17"),
            title = "Look para presentar el TP en la facu",
            type = OutfitPostType.PLANNED,
            createdAt = "27 de mayo de 2026",
            plannedDate = "4 de junio de 2026",
            likedBy = listOf(
                Like("like_25", summary(MARIA_USER_ID), "27 de mayo de 2026"),
                Like("like_26", summary("user_6"), "27 de mayo de 2026")
            ),
            comments = listOf(
                Comment("comment_12", summary(MARIA_USER_ID), "Muy prolijo, re va para presentar!", "27 de mayo de 2026")
            )
        ),
        OutfitPost(
            id = "post_18",
            author = summary(JUAN_USER_ID),
            outfit = outfit("outfit_18"),
            title = "Domingo comodo pero presentable",
            type = OutfitPostType.FAVORITE,
            createdAt = "24 de mayo de 2026",
            likedBy = listOf(
                Like("like_27", summary("user_2"), "24 de mayo de 2026")
            ),
            comments = emptyList()
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

    private val mutableNotifications = defaultNotifications().toMutableList()

    val notifications: List<Notification>
        get() = mutableNotifications.toList()

    private fun defaultNotifications(): List<Notification> = listOf(
        Notification(
            id = "notification_0",
            receiver = currentUser.toSummary(),
            sender = summary("user_6"),
            type = NotificationType.POST_LIKE,
            postId = "post_1",
            createdAt = "hace 30 minutos"
        ),
        Notification(
            id = "notification_1",
            receiver = currentUser.toSummary(),
            sender = summary("user_4"),
            type = NotificationType.POST_LIKE,
            postId = "post_1",
            createdAt = "hace 43 minutos"
        ),
        Notification(
            id = "notification_2",
            receiver = currentUser.toSummary(),
            sender = summary("user_4"),
            type = NotificationType.POST_COMMENT,
            postId = "post_1",
            commentId = "comment_1",
            createdAt = "hace 43 minutos"
        ),
        Notification(
            id = "notification_3",
            receiver = currentUser.toSummary(),
            sender = summary("user_6"),
            type = NotificationType.FRIEND_REQUEST_RECEIVED,
            friendRequestId = "request_2",
            createdAt = "hace 57 minutos"
        ),
        Notification(
            id = "notification_4",
            receiver = currentUser.toSummary(),
            sender = summary("user_3"),
            type = NotificationType.FRIEND_REQUEST_RECEIVED,
            friendRequestId = "request_1",
            createdAt = "hace 3 horas"
        ),
        Notification(
            id = "notification_5",
            receiver = currentUser.toSummary(),
            sender = summary("user_4"),
            type = NotificationType.FRIEND_REQUEST_ACCEPTED,
            friendRequestId = "request_3",
            createdAt = "hace 5 horas",
            read = true
        )
    )

    fun userById(userId: String): User? = users.firstOrNull { it.id == userId }

    fun authUserById(userId: String): User? =
        authUsers.firstOrNull { it.user.id == userId }?.user

    fun findAuthUser(email: String, password: String): User? =
        authUsers.firstOrNull { it.user.email == email && it.password == password }?.user

    fun isUsernameAvailable(username: String): Boolean =
        authUsers.none { it.user.username.lowercase() == username.lowercase() } &&
            users.none { it.username.lowercase() == username.lowercase() }

    fun registerAuthUser(
        email: String,
        password: String,
        username: String
    ): User {
        val id = "auth_${authUsers.size + 1}"
        val user = User(
            id = id,
            email = email,
            profile = UserProfile(
                id = id,
                fullName = username,
                username = username,
                birthDate = "",
                bio = "",
                avatarImageResId = R.drawable.avatar_default,
                bannerImageResId = R.drawable.banner_default
            )
        )
        authUsers.add(MockAuthUser(user = user, password = password))
        users.add(user)
        friendIdsByUser[user.id] = mutableSetOf()
        return user
    }

    fun summary(userId: String): UserSummary =
        requireNotNull(userById(userId)) { "Unknown mock user id: $userId" }.toSummary()

    fun outfit(outfitId: String): Outfit =
        requireNotNull(outfits.firstOrNull { it.id == outfitId }) { "Unknown mock outfit id: $outfitId" }

    fun friendIds(userId: String): Set<String> =
        friendIdsByUser[userId].orEmpty().toSet()

    fun currentFriendIds(): Set<String> = friendIds(CURRENT_USER_ID)

    fun addFriend(userId: String, friendId: String) {
        if (userId == friendId || users.none { it.id == userId } || users.none { it.id == friendId }) return

        friendIdsByUser.getOrPut(userId) { mutableSetOf() }.add(friendId)
        friendIdsByUser.getOrPut(friendId) { mutableSetOf() }.add(userId)
    }

    fun addCurrentUserFriend(friendId: String) {
        addFriend(CURRENT_USER_ID, friendId)
    }

    fun removeFriend(userId: String, friendId: String) {
        friendIdsByUser[userId]?.remove(friendId)
        friendIdsByUser[friendId]?.remove(userId)
    }

    fun friendRequestById(requestId: String): FriendRequest? =
        mutableFriendRequests.firstOrNull { it.id == requestId }

    fun pendingOutgoingFriendRequest(senderId: String, receiverId: String): FriendRequest? =
        mutableFriendRequests.firstOrNull {
            it.sender.id == senderId &&
                it.receiver.id == receiverId &&
                it.status == FriendRequestStatus.PENDING
        }

    fun pendingIncomingFriendRequest(receiverId: String, senderId: String): FriendRequest? =
        mutableFriendRequests.firstOrNull {
            it.sender.id == senderId &&
                it.receiver.id == receiverId &&
                it.status == FriendRequestStatus.PENDING
        }

    fun sendFriendRequest(senderId: String, receiverId: String): FriendRequest? {
        if (senderId == receiverId || isFriend(senderId, receiverId)) return null

        pendingOutgoingFriendRequest(senderId, receiverId)?.let { return it }

        val sender = userById(senderId)?.toSummary() ?: return null
        val receiver = userById(receiverId)?.toSummary() ?: return null
        val request = FriendRequest(
            id = "request_${mutableFriendRequests.size + 1}",
            sender = sender,
            receiver = receiver,
            status = FriendRequestStatus.PENDING,
            createdAt = "ahora"
        )
        mutableFriendRequests.add(0, request)
        mutableNotifications.add(
            0,
            Notification(
                id = "notification_${mutableNotifications.size + 1}",
                receiver = receiver,
                sender = sender,
                type = NotificationType.FRIEND_REQUEST_RECEIVED,
                friendRequestId = request.id,
                createdAt = "ahora"
            )
        )
        return request
    }

    fun respondToFriendRequest(requestId: String, accepted: Boolean): FriendRequest? {
        val index = mutableFriendRequests.indexOfFirst { it.id == requestId }
        if (index == -1) return null

        val request = mutableFriendRequests[index]
        if (request.status != FriendRequestStatus.PENDING) return request

        val updatedRequest = request.copy(
            status = if (accepted) FriendRequestStatus.ACCEPTED else FriendRequestStatus.REJECTED,
            respondedAt = "ahora"
        )
        mutableFriendRequests[index] = updatedRequest

        if (accepted) {
            addFriend(request.sender.id, request.receiver.id)
            mutableNotifications.add(
                0,
                Notification(
                    id = "notification_${mutableNotifications.size + 1}",
                    receiver = request.sender,
                    sender = request.receiver,
                    type = NotificationType.FRIEND_REQUEST_ACCEPTED,
                    friendRequestId = request.id,
                    createdAt = "ahora"
                )
            )
        }

        return updatedRequest
    }

    fun markNotificationsAsRead(userId: String) {
        mutableNotifications.replaceAll { notification ->
            if (notification.receiver.id == userId) notification.copy(read = true) else notification
        }
    }

    fun isFriend(userId: String, otherUserId: String): Boolean =
        otherUserId in friendIds(userId)

    fun removeCurrentUserFriend(friendId: String) {
        removeFriend(CURRENT_USER_ID, friendId)
    }

    fun resetCurrentUserFriends() {
        friendIdsByUser.clear()
        friendIdsByUser.putAll(buildFriendIdsByUser())
        mutableFriendRequests.clear()
        mutableFriendRequests.addAll(defaultFriendRequests())
        mutableNotifications.clear()
        mutableNotifications.addAll(defaultNotifications())
    }

    private fun buildFriendIdsByUser(): MutableMap<String, MutableSet<String>> {
        val friendships = mutableMapOf<String, MutableSet<String>>()
        defaultFriendIdsByUser.forEach { (userId, friendIds) ->
            friendIds.forEach { friendId ->
                friendships.getOrPut(userId) { mutableSetOf() }.add(friendId)
                friendships.getOrPut(friendId) { mutableSetOf() }.add(userId)
            }
        }
        return friendships
    }

    private fun user(
        id: String,
        fullName: String,
        username: String,
        bio: String = DEFAULT_USER_BIO,
        birthDate: String = DEFAULT_USER_BIRTH_DATE
    ): User = User(
        id = id,
        email = "$id@closify.com",
        profile = UserProfile(
            id = id,
            fullName = fullName,
            username = username,
            birthDate = birthDate,
            bio = bio,
            avatarImageResId = R.drawable.avatar_default,
            bannerImageResId = R.drawable.banner_default
        ),
        createdAt = "1 de mayo de 2026"
    )

    private fun garment(
        id: String,
        name: String,
        category: GarmentCategory,
        ownerUserId: String = CURRENT_USER_ID,
        imageName: String = id
    ): Garment = Garment(
        id = id,
        ownerUserId = ownerUserId,
        name = name,
        category = category,
        imageUrl = "$RESOURCE_PREFIX$imageName",
        suitableWeather = setOf(WeatherCondition.ANY),
        suitableOccasions = setOf(Occasion.ANY),
        createdAt = "1 de mayo de 2026"
    )

    private fun outfit(
        id: String,
        name: String?,
        createdAt: String,
        vararg garmentIds: String
    ): Outfit = ownedOutfit(id, name, createdAt, CURRENT_USER_ID, *garmentIds)

    private fun ownedOutfit(
        id: String,
        name: String?,
        createdAt: String,
        ownerUserId: String,
        vararg garmentIds: String
    ): Outfit {
        val garmentsById = garments.associateBy { it.id }
        return Outfit(
            id = id,
            garments = garmentIds.mapNotNull { garmentsById[it] },
            ownerUserId = ownerUserId,
            name = name,
            createdAt = createdAt
        )
    }
}
