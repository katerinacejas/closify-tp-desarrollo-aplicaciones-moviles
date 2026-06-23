package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.GarmentEntity
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.google.firebase.firestore.DocumentSnapshot

fun GarmentEntity.toDomain(): Garment = Garment(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    category = GarmentCategory.valueOf(category),
    imageUrl = imageUrl,
    suitableWeather = suitableWeather.split(",").mapNotNull {
        runCatching { WeatherCondition.valueOf(it) }.getOrNull()
    }.toSet(),
    suitableOccasions = suitableOccasions.split(",").mapNotNull {
        runCatching { Occasion.valueOf(it) }.getOrNull()
    }.toSet(),
    createdAt = createdAt
)

fun Garment.toEntity(): GarmentEntity = GarmentEntity(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    category = category.name,
    imageUrl = imageUrl,
    suitableWeather = suitableWeather.joinToString(",") { it.name },
    suitableOccasions = suitableOccasions.joinToString(",") { it.name },
    createdAt = createdAt
)

fun Garment.toFirestoreMap(): Map<String, Any> = mapOf(
    "ownerUserId" to ownerUserId,
    "name" to name,
    "category" to category.name,
    "imageUrl" to imageUrl,
    "suitableWeather" to suitableWeather.map { it.name },
    "suitableOccasions" to suitableOccasions.map { it.name },
    "createdAt" to createdAt
)

fun DocumentSnapshot.toGarmentEntity(): GarmentEntity? {
    return try {
        GarmentEntity(
            id = id,
            ownerUserId = getString("ownerUserId") ?: "",
            name = getString("name") ?: "",
            category = getString("category") ?: "",
            imageUrl = getString("imageUrl") ?: "",
            suitableWeather = (get("suitableWeather") as? List<*>)
                ?.joinToString(",") { it.toString() } ?: "",
            suitableOccasions = (get("suitableOccasions") as? List<*>)
                ?.joinToString(",") { it.toString() } ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}
