package com.closify.myapplication.domain.model

data class SuggestedOutfit(
    val id: String,
    val garments: List<Garment>,
    val climate: WeatherCondition,
    val occasion: Occasion,
    val generatedAt: String,
    val isSavedAsFavorite: Boolean = false
)
