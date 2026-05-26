package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Outfit

class OutfitRepository {

    companion object {
        val instance = OutfitRepository()
    }

    // Outfits generados por HomeViewModel — leídos por OutfitResultViewModel
    var currentOutfits: List<Outfit> = emptyList()

    // Favoritos guardados en memoria
    // TODO: reemplazar por Firebase Firestore
    private val _favoriteOutfits = mutableListOf<Outfit>()
    val favoriteOutfits: List<Outfit> get() = _favoriteOutfits.toList()

    fun saveFavorites(outfits: List<Outfit>) {
        outfits.forEach { outfit ->
            if (_favoriteOutfits.none { it.id == outfit.id }) {
                _favoriteOutfits.add(outfit)
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
}
