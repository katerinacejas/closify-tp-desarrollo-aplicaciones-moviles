package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey val id: String,
    val ownerUserId: String,
    val name: String?,
    val garmentIds: String,
    val createdAt: String
)
