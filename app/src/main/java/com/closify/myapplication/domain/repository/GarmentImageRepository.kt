package com.closify.myapplication.domain.repository

interface GarmentImageRepository {
    suspend fun prepareImagePreview(imageUri: String): Result<String>
    suspend fun storeGarmentImage(imageUri: String): String
}
