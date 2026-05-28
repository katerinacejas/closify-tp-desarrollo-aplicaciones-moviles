package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition

class GarmentRepository {

    companion object {
        val instance = GarmentRepository()
    }

    fun addGarment(garment: Garment) {
        MockClosifyData.garments.add(garment)
    }

    fun getAllByUserId(userId: String = UserRepository.instance.currentUserId): List<Garment> =
        MockClosifyData.garments.filter { it.ownerUserId == userId }

    fun getByCategory(category: GarmentCategory, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { it.category == category }

    fun getByOccasion(occasion: Occasion, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { occasion in it.suitableOccasions || Occasion.ANY in it.suitableOccasions }

    fun getByWeather(weather: WeatherCondition, userId: String = UserRepository.instance.currentUserId): List<Garment> =
        getAllByUserId(userId).filter { weather in it.suitableWeather || WeatherCondition.ANY in it.suitableWeather }
}
