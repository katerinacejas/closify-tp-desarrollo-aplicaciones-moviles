package com.closify.myapplication.domain.usecase

import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.domain.model.Garment

class SaveGarmentUseCase(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance
) {
    operator fun invoke(garment: Garment) {
        garmentRepository.addGarment(garment)
    }
}
