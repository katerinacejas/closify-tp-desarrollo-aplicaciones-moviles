package com.closify.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfit_posts")
data class OutfitPostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val outfitId: String,
    val title: String?,
    val type: String, // FAVORITE, PLANNED
    val createdAt: String,
    val plannedDate: String? = null
)
