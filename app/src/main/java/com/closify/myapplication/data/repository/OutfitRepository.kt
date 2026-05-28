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
                MockClosifyData.outfitPosts.add(
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
}
