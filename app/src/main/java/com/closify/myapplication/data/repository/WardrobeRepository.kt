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
        GarmentRepository.instance.getAllForUser(userId)

    fun getGarmentsByCategory(category: GarmentCategory): List<Garment> =
        getAllGarments().filter { it.category == category }

    fun getGarmentsByOccasion(occasion: Occasion): List<Garment> =
        getAllGarments().filter { occasion in it.suitableOccasions || Occasion.ANY in it.suitableOccasions }

    fun getGarmentsByWeather(weather: WeatherCondition): List<Garment> =
        getAllGarments().filter { weather in it.suitableWeather || WeatherCondition.ANY in it.suitableWeather }

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
