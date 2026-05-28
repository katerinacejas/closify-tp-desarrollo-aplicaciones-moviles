package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.WeatherCondition

class WardrobeRepository {

    companion object {
        val instance = WardrobeRepository()
    }

    fun getAllGarments(userId: String = MockClosifyData.CURRENT_USER_ID): List<Garment> =
        MockClosifyData.garments.filter { it.ownerUserId == userId }

    fun getGarmentsByCategory(category: GarmentCategory): List<Garment> =
        getAllGarments().filter { it.category == category }

    fun getGarmentsByOccasion(occasion: Occasion): List<Garment> =
        getAllGarments().filter { 
            if (occasion == Occasion.ANY) {
                Occasion.ANY in it.suitableOccasions
            } else {
                occasion in it.suitableOccasions
            }
        }

    fun getGarmentsByWeather(weather: WeatherCondition): List<Garment> =
        getAllGarments().filter { 
            if (weather == WeatherCondition.ANY) {
                WeatherCondition.ANY in it.suitableWeather
            } else {
                weather in it.suitableWeather
            }
        }

    fun getGarmentsByName(query: String): List<Garment> =
        getAllGarments().filter { it.name.contains(query, ignoreCase = true) }

    fun calculateWardrobeUsagePercentage(posts: List<OutfitPost>): Int {
        val totalGarments = getAllGarments().size
        if (totalGarments == 0) return 0

        val usedGarmentIds = posts
            .flatMap { it.outfit.garments }
            .map { it.id }
            .toSet()

        return ((usedGarmentIds.size * 100) / totalGarments).coerceIn(0, 100)
    }
}
