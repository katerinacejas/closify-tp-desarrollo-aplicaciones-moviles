package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserProfile

data class PublicProfileStats(
    val garmentsCount: Int,
    val wardrobeUsagePercentage: Int,
    val favoriteOutfitsCount: Int,
    val plannedOutfitsCount: Int
)

data class ProfileStats(
    val garmentsCount: Int,
    val wardrobeUsagePercentage: Int,
    val favoriteOutfitsCount: Int,
    val plannedOutfitsCount: Int
)

class ProfileRepository(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val wardrobeRepository: WardrobeRepository = WardrobeRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance
) {

    companion object {
        val instance = ProfileRepository()
    }

    fun getProfile(userId: String = MockClosifyData.CURRENT_USER_ID): UserProfile =
        MockClosifyData.userById(userId)?.profile
            ?: UserRepository.instance.getCurrentUser()?.profile
            ?: MockClosifyData.currentUser.profile

    fun getUserProfile(userId: String): UserProfile? =
        MockClosifyData.userById(userId)?.profile

    fun getWardrobeGarments(userId: String = MockClosifyData.CURRENT_USER_ID): List<Garment> =
        garmentRepository.getAllByUserId(userId)

    fun getWardrobeUsagePercentage(userId: String = MockClosifyData.CURRENT_USER_ID): Int =
        wardrobeRepository.calculateWardrobeUsagePercentage(outfitPostRepository.getPostsByUser(userId))

    fun publicProfileBaseGarmentsCount(): Int =
        MockClosifyData.PUBLIC_PROFILE_BASE_GARMENTS_COUNT

    fun getProfileStats(userId: String = MockClosifyData.CURRENT_USER_ID): ProfileStats {
        val posts = outfitPostRepository.getPostsByUser(userId)
        return ProfileStats(
            garmentsCount = getWardrobeGarments(userId).size,
            wardrobeUsagePercentage = getWardrobeUsagePercentage(userId),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED }
        )
    }

    fun getPublicProfileStats(userId: String): PublicProfileStats {
        val posts = outfitPostRepository.getPostsByUser(userId)
        val usedGarments = posts.flatMap { it.outfit.garments }.map { it.id }.toSet()
        val garmentsCount = publicProfileBaseGarmentsCount() + usedGarments.size

        return PublicProfileStats(
            garmentsCount = garmentsCount,
            wardrobeUsagePercentage = if (garmentsCount == 0) 0 else ((usedGarments.size * 100) / garmentsCount).coerceIn(0, 100),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED }
        )
    }
}
