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

class ProfileRepository private constructor(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance,
    private val wardrobeRepository: WardrobeRepository = WardrobeRepository.instance,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) {

    companion object {
        @Volatile private var _instance: ProfileRepository? = null

        fun initialize() {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = ProfileRepository()
                    }
                }
            }
        }

        val instance: ProfileRepository
            get() = _instance ?: error("ProfileRepository.initialize() no fue llamado.")
    }

    suspend fun getProfile(userId: String = userRepository.currentUserId): UserProfile? {
        val user = userRepository.getCurrentUser()
        if (user != null && user.id == userId) return user.profile
        
        // Si no es el actual, buscamos por ID (esto debería estar en UserRepository realmente)
        return userRepository.getUserById(userId)?.profile
    }

    suspend fun getUserProfile(userId: String): UserProfile? = getProfile(userId)

    suspend fun getWardrobeGarments(userId: String = userRepository.currentUserId): List<Garment> =
        garmentRepository.getAllByUserId(userId)

    suspend fun getWardrobeUsagePercentage(userId: String = userRepository.currentUserId): Int =
        wardrobeRepository.calculateWardrobeUsagePercentage(outfitPostRepository.getPostsByUser(userId), userId)

    suspend fun getProfileStats(userId: String = userRepository.currentUserId): ProfileStats {
        val posts = outfitPostRepository.getPostsByUser(userId)
        val garments = getWardrobeGarments(userId)
        return ProfileStats(
            garmentsCount = garments.size,
            wardrobeUsagePercentage = getWardrobeUsagePercentage(userId),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED }
        )
    }

    suspend fun getPublicProfileStats(userId: String): PublicProfileStats {
        val posts = outfitPostRepository.getPostsByUser(userId)
        val garments = getWardrobeGarments(userId)
        
        val usedGarmentsCount = posts.flatMap { it.outfit.garments }.map { it.id }.toSet().size
        val totalGarmentsCount = garments.size

        return PublicProfileStats(
            garmentsCount = totalGarmentsCount,
            wardrobeUsagePercentage = if (totalGarmentsCount == 0) 0 else ((usedGarmentsCount * 100) / totalGarmentsCount).coerceIn(0, 100),
            favoriteOutfitsCount = posts.count { it.type == OutfitPostType.FAVORITE },
            plannedOutfitsCount = posts.count { it.type == OutfitPostType.PLANNED }
        )
    }
}
