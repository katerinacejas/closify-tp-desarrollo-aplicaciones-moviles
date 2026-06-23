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

    fun getProfile(userId: String = UserRepository.instance.currentUserId): UserProfile =
        MockClosifyData.userById(userId)?.profile
            ?: UserRepository.instance.getCurrentUser()?.profile
            ?: MockClosifyData.currentUser.profile

    fun getUserProfile(userId: String): UserProfile? =
        MockClosifyData.userById(userId)?.profile

    suspend fun getWardrobeGarments(userId: String = UserRepository.instance.currentUserId): List<Garment> =
        garmentRepository.getAllByUserId(userId)

    suspend fun getWardrobeUsagePercentage(userId: String = UserRepository.instance.currentUserId): Int =
        wardrobeRepository.calculateWardrobeUsagePercentage(outfitPostRepository.getPostsByUser(userId), userId)

    fun publicProfileBaseGarmentsCount(): Int =
        MockClosifyData.PUBLIC_PROFILE_BASE_GARMENTS_COUNT

    suspend fun getProfileStats(userId: String = UserRepository.instance.currentUserId): ProfileStats {
        val posts = outfitPostRepository.getPostsByUser(userId)
        return ProfileStats(
            garmentsCount = getWardrobeGarments(userId).size,
            wardrobeUsagePercentage = getWardrobeUsagePercentage(userId),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED }
        )
    }

    suspend fun getPublicProfileStats(userId: String): PublicProfileStats {
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
