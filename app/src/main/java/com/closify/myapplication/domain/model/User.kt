package com.closify.myapplication.domain.model

import java.time.LocalDate

data class User(
    val id: String,
    val name: String,
    val username: String = "",
    val bio: String = "",
    val birthdate: LocalDate? = null,
    val clothes: List<Garment> = emptyList(),
    // val friends: Set<User> = emptySet(), // Evitamos recursividad por ahora o usamos IDs
    // val outfitsFav: Set<Outfit> = emptySet(),
    // val outfitPlanificado: Set<Outfit> = emptySet(),
    // val notifications: List<Notification> = emptyList()
)
