package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.OutfitEntity
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.Outfit
import com.google.firebase.firestore.DocumentSnapshot

fun OutfitEntity.toDomain(garments: List<Garment>): Outfit = Outfit(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    garments = garments,
    createdAt = createdAt
)

fun Outfit.toEntity(): OutfitEntity = OutfitEntity(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    garmentIds = garments.joinToString(",") { it.id },
    createdAt = createdAt
)

fun Outfit.toFirestoreMap(): Map<String, Any> = mapOf(
    "ownerUserId" to ownerUserId,
    "name" to (name ?: ""),
    "garmentIds" to garments.map { it.id },
    "createdAt" to createdAt
)

fun DocumentSnapshot.toOutfitEntity(): OutfitEntity? {
    return try {
        OutfitEntity(
            id = id,
            ownerUserId = getString("ownerUserId") ?: "",
            name = getString("name")?.ifEmpty { null },
            garmentIds = (get("garmentIds") as? List<*>)
                ?.joinToString(",") { it.toString() } ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}
