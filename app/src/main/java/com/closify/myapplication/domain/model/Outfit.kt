package com.closify.myapplication.domain.model

data class Outfit(
    val id: String,
    val garments: List<Garment>,
    val ownerUserId: String = "",
    val name: String? = null,
    val createdAt: String = ""
)
