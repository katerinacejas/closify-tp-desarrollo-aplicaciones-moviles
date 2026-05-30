package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.UserProfile

class ProfileRepository(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val wardrobeRepository: WardrobeRepository = WardrobeRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance
) {

    companion object {
        val instance = ProfileRepository()
    }

    fun getProfile(userId: String = MockClosifyData.CURRENT_USER_ID): UserProfile =
        requireNotNull(MockClosifyData.userById(userId)).profile

    fun getUserProfile(userId: String): UserProfile? =
        MockClosifyData.userById(userId)?.profile

    fun getWardrobeGarments(userId: String = MockClosifyData.CURRENT_USER_ID): List<Garment> =
        garmentRepository.getAllByUserId(userId)

    fun getWardrobeUsagePercentage(userId: String = MockClosifyData.CURRENT_USER_ID): Int =
        wardrobeRepository.calculateWardrobeUsagePercentage(outfitPostRepository.getPostsByUser(userId))

    fun publicProfileBaseGarmentsCount(): Int =
        MockClosifyData.PUBLIC_PROFILE_BASE_GARMENTS_COUNT
}
