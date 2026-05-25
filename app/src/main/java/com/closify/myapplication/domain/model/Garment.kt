package com.closify.myapplication.domain.model

data class Garment(
    val id: String,
    val name: String,
    val category: GarmentCategory,
    val color: GarmentColor,
    val imageUrl: String,               // local: "android.resource://..." | remoto: "https://..."
    val suitableWeather: Set<WeatherCondition>,
    val suitableOccasions: Set<Occasion>
)
