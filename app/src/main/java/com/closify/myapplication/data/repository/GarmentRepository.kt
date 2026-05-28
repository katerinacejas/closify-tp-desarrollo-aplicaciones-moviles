package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment

class GarmentRepository(
    private val wardrobeRepository: WardrobeRepository = WardrobeRepository.instance
) {

    companion object {
        val instance = GarmentRepository()
    }

    fun getAll(): List<Garment> = wardrobeRepository.getAllGarments()
}
