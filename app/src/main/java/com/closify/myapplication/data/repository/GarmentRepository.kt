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

    fun getAllByUserId(userId: String = MockClosifyData.CURRENT_USER_ID): List<Garment> =
        MockClosifyData.garments.filter { it.ownerUserId == userId }
}
