package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

data class PlannerGarmentGroups(
    val topAndOuterwear: List<Garment>,
    val bottoms: List<Garment>,
    val footwear: List<Garment>,
    val fullBody: List<Garment>
)

class GarmentRepository {

    companion object {
        val instance = GarmentRepository()
    }

    fun addGarment(garment: Garment) {
        MockClosifyData.garments.add(garment)
    }

    fun createGarment(
        ownerUserId: String,
        name: String,
        category: GarmentCategory,
        imageUrl: String,
        suitableWeather: Set<WeatherCondition>,
        suitableOccasions: Set<Occasion>
    ): Garment {
        val garment = Garment(
            id = UUID.randomUUID().toString(),
            ownerUserId = ownerUserId,
            name = name.trim(),
            category = category,
            imageUrl = imageUrl,
            suitableWeather = suitableWeather.ifEmpty { setOf(WeatherCondition.ANY) },
            suitableOccasions = suitableOccasions.ifEmpty { setOf(Occasion.ANY) },
            createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "AR"))
            )
        )
        addGarment(garment)
        return garment
    }

    fun getAllByUserId(userId: String = UserRepository.instance.currentUserId): List<Garment> =
        MockClosifyData.garments.filter { it.ownerUserId == userId }

    fun getByCategory(category: GarmentCategory, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { it.category == category }

    fun getByOccasion(occasion: Occasion, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { occasion in it.suitableOccasions || Occasion.ANY in it.suitableOccasions }

    fun getByWeather(weather: WeatherCondition, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { weather in it.suitableWeather || WeatherCondition.ANY in it.suitableWeather }

    fun getCategoryCounts(userId: String = UserRepository.instance.currentUserId): Map<GarmentCategory, Int> =
        getAllByUserId(userId).groupBy { it.category }.mapValues { it.value.size }

    fun getWeatherCounts(userId: String = UserRepository.instance.currentUserId): Map<WeatherCondition, Int> =
        WeatherCondition.entries.associateWith { getByWeather(it, userId).size }

    fun getOccasionCounts(userId: String = UserRepository.instance.currentUserId): Map<Occasion, Int> =
        Occasion.entries.associateWith { getByOccasion(it, userId).size }

    fun searchByName(query: String, userId: String = UserRepository.instance.currentUserId): List<Garment> {
        val cleanQuery = query.trim()
        val garments = getAllByUserId(userId)
        return if (cleanQuery.isEmpty()) {
            garments
        } else {
            garments.filter { it.name.contains(cleanQuery, ignoreCase = true) }
        }
    }

    fun getById(id: String, userId: String = UserRepository.instance.currentUserId): Garment? =
        getAllByUserId(userId).firstOrNull { it.id == id }

    fun deleteGarment(id: String, userId: String = UserRepository.instance.currentUserId): Boolean =
        MockClosifyData.garments.removeIf { it.id == id && it.ownerUserId == userId }

    fun getPlannerGroups(userId: String = UserRepository.instance.currentUserId): PlannerGarmentGroups {
        val garments = getAllByUserId(userId)
        return PlannerGarmentGroups(
            topAndOuterwear = garments.filter {
                it.category == GarmentCategory.TOP || it.category == GarmentCategory.OUTERWEAR
            },
            bottoms = garments.filter { it.category == GarmentCategory.BOTTOM },
            footwear = garments.filter { it.category == GarmentCategory.FOOTWEAR },
            fullBody = garments.filter { it.category == GarmentCategory.FULL_BODY }
        )
    }
}
