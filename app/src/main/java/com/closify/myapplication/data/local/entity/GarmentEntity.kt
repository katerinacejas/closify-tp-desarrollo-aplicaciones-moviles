package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garments")
data class GarmentEntity(
    @PrimaryKey val id: String,
    val ownerUserId: String,
    val name: String,
    val category: String,
    val imageUrl: String,
    val suitableWeather: String,
    val suitableOccasions: String,
    val createdAt: String
)
