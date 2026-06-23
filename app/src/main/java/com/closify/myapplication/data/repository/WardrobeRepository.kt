package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.OutfitPost

class WardrobeRepository {

    companion object {
        val instance = WardrobeRepository()
    }

    suspend fun calculateWardrobeUsagePercentage(posts: List<OutfitPost>, userId: String): Int {
        val totalGarments = GarmentRepository.instance.getAllByUserId(userId).size
        if (totalGarments == 0) return 0

        val usedGarmentIds = posts
            .flatMap { it.outfit.garments }
            .map { it.id }
            .toSet()

        return ((usedGarmentIds.size * 100) / totalGarments).coerceIn(0, 100)
    }
}
