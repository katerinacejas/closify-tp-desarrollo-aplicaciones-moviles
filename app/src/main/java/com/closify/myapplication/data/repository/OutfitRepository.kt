package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.SuggestedOutfit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class OutfitRepository {

    companion object {
        val instance = OutfitRepository()
    }

    // Outfits generados por HomeViewModel — leídos por OutfitResultViewModel
    var currentOutfits: List<Outfit> = emptyList()

    // Outfits seleccionados para guardar — leídos por SaveFavoritesViewModel
    var pendingFavorites: List<Outfit> = emptyList()

    // Favoritos guardados en memoria
    // TODO: reemplazar por Firebase Firestore
    private val _favoriteOutfits = mutableListOf<Outfit>()
    val favoriteOutfits: List<Outfit> get() = _favoriteOutfits.toList()

    fun saveFavorites(outfits: List<Outfit>) {
        val author = UserRepository.instance.getCurrentUserOrDefault().toSummary()
        val createdAt = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "AR"))
        )
        outfits.forEach { outfit ->
            if (_favoriteOutfits.none { it.id == outfit.id }) {
                _favoriteOutfits.add(outfit)
                MockClosifyData.addOutfitPost(
                    OutfitPost(
                        id = UUID.randomUUID().toString(),
                        author = author,
                        outfit = outfit,
                        title = outfit.name,
                        type = OutfitPostType.FAVORITE,
                        createdAt = createdAt
                    )
                )
            }
        }
    }

    fun toggleFavorite(outfitId: String) {
        val existing = _favoriteOutfits.find { it.id == outfitId }
        if (existing != null) {
            _favoriteOutfits.remove(existing)
        } else {
            currentOutfits.find { it.id == outfitId }?.let {
                _favoriteOutfits.add(it)
            }
        }
    }

    fun isFavorite(outfitId: String): Boolean =
        _favoriteOutfits.any { it.id == outfitId }

    fun getSuggestedOutfits(): List<SuggestedOutfit> =
        MockClosifyData.suggestedOutfits

    fun getFavoritePosts(userId: String = MockClosifyData.CURRENT_USER_ID): List<OutfitPost> =
        MockClosifyData.outfitPosts.filter {
            it.author.id == userId && it.type == OutfitPostType.FAVORITE
        }

    fun getPlannedPosts(userId: String = MockClosifyData.CURRENT_USER_ID): List<OutfitPost> =
        MockClosifyData.outfitPosts.filter {
            it.author.id == userId && it.type == OutfitPostType.PLANNED
        }

    fun savePlannedOutfitPost(
        userId: String,
        title: String?,
        outfit: Outfit,
        plannedDate: String,
        createdAt: String
    ): OutfitPost? {
        val author = MockClosifyData.userById(userId)?.toSummary() ?: return null
        val post = OutfitPost(
            id = "planned_post_${MockClosifyData.outfitPosts.size + 1}",
            author = author,
            outfit = outfit.copy(ownerUserId = userId),
            title = title?.take(100)?.ifBlank { null },
            type = OutfitPostType.PLANNED,
            createdAt = createdAt,
            plannedDate = plannedDate
        )
        return MockClosifyData.addOutfitPost(post)
    }

    fun updatePlannedOutfitPost(
        postId: String,
        title: String?,
        outfit: Outfit,
        plannedDate: String
    ): OutfitPost? {
        val currentPost = MockClosifyData.outfitPosts.firstOrNull { it.id == postId } ?: return null
        val updatedPost = currentPost.copy(
            outfit = outfit.copy(ownerUserId = currentPost.author.id),
            title = title?.take(100)?.ifBlank { null },
            plannedDate = plannedDate
        )
        return MockClosifyData.updateOutfitPost(updatedPost)
    }

    fun deletePlannedOutfitPost(postId: String) {
        MockClosifyData.deleteOutfitPost(postId)
    }

    fun getPlannedPostById(postId: String): OutfitPost? =
        MockClosifyData.outfitPosts.firstOrNull { it.id == postId && it.type == OutfitPostType.PLANNED }
}
