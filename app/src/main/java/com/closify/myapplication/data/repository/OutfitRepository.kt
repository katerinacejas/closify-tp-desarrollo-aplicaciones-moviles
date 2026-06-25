package com.closify.myapplication.data.repository

import android.content.Context
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toOutfitEntity
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.SuggestedOutfit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class OutfitRepository private constructor(
    context: Context,
    private val outfitPostRepository: OutfitPostRepository = OutfitPostRepository.instance
) {

    companion object {
        @Volatile private var _instance: OutfitRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = OutfitRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: OutfitRepository
            get() = _instance ?: error("OutfitRepository.initialize(context) no fue llamado.")
    }

    private val outfitDao = AppDatabase.getInstance(context).outfitDao()
    private val garmentDao = AppDatabase.getInstance(context).garmentDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Estado temporal de navegación — no necesita persistirse
    var currentOutfits: List<Outfit> = emptyList()
    var pendingFavorites: List<Outfit> = emptyList()

    fun setPendingFavorites(outfits: List<Outfit>, favoriteIds: Set<String>) {
        pendingFavorites = outfits.filter { it.id in favoriteIds }
    }

    suspend fun syncFromFirestore(userId: String) {
        try {
            val snapshot = firestore.collection("users/$userId/outfits").get().await()
            val entities = snapshot.documents.mapNotNull { it.toOutfitEntity() }
            outfitDao.upsertAll(entities)
        } catch (e: Exception) {
            // Sin conexión — Room ya tiene los datos del último sync
        }
    }

    suspend fun isFavorite(outfitId: String): Boolean =
        outfitDao.getById(outfitId) != null

    suspend fun saveFavorites(outfits: List<Outfit>) {
        val userId = UserRepository.instance.currentUserId
        val author = UserRepository.instance.getCurrentUserOrDefault().toSummary()
        val createdAt = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
        )
        outfits.forEach { outfit ->
            val outfitWithOwner = outfit.copy(ownerUserId = userId)
            if (outfitDao.getById(outfitWithOwner.id) == null) {
                outfitDao.upsert(outfitWithOwner.toEntity())
                scope.launch {
                    firestore.collection("users/$userId/outfits")
                        .document(outfitWithOwner.id)
                        .set(outfitWithOwner.toFirestoreMap())
                        .await()
                }
                outfitPostRepository.addPost(
                    OutfitPost(
                        id = UUID.randomUUID().toString(),
                        author = author,
                        outfit = outfitWithOwner,
                        title = outfitWithOwner.name,
                        type = OutfitPostType.FAVORITE,
                        createdAt = createdAt
                    )
                )
            }
        }
    }

    suspend fun saveFavorites(outfits: List<Outfit>, outfitNames: Map<String, String>) {
        saveFavorites(
            outfits.map { outfit ->
                outfit.copy(name = outfitNames[outfit.id]?.trim()?.ifEmpty { null })
            }
        )
    }

    suspend fun toggleFavorite(outfitId: String) {
        val existing = outfitDao.getById(outfitId)
        if (existing != null) {
            outfitDao.deleteById(outfitId)
            scope.launch {
                val userId = UserRepository.instance.currentUserId
                firestore.collection("users/$userId/outfits").document(outfitId).delete().await()
            }
        } else {
            currentOutfits.find { it.id == outfitId }?.let { outfit ->
                outfitDao.upsert(outfit.toEntity())
                scope.launch {
                    firestore.collection("users/${outfit.ownerUserId}/outfits")
                        .document(outfit.id)
                        .set(outfit.toFirestoreMap())
                        .await()
                }
            }
        }
    }

    suspend fun getFavoriteOutfits(userId: String): List<Outfit> {
        val entities = outfitDao.getAllByUserId(userId)
        return entities.mapNotNull { entity ->
            val garmentIds = entity.garmentIds.split(",").filter { it.isNotBlank() }
            val garments = garmentIds.mapNotNull { garmentDao.getById(it)?.toDomain() }
            entity.toDomain(garments)
        }
    }

    fun getSuggestedOutfits(): List<SuggestedOutfit> =
        MockClosifyData.suggestedOutfits

    suspend fun getFavoritePosts(userId: String = UserRepository.instance.currentUserId): List<OutfitPost> =
        outfitPostRepository.getPostsByUser(userId).filter { it.type == OutfitPostType.FAVORITE }

    suspend fun getPlannedPosts(userId: String = UserRepository.instance.currentUserId): List<OutfitPost> =
        outfitPostRepository.getPostsByUser(userId).filter { it.type == OutfitPostType.PLANNED }

    suspend fun savePlannedOutfitPost(
        userId: String,
        title: String?,
        outfit: Outfit,
        plannedDate: String,
        createdAt: String
    ): OutfitPost? {
        val author = UserRepository.instance.getCurrentUser()?.toSummary()
            ?: MockClosifyData.userById(userId)?.toSummary()
            ?: return null
        val post = OutfitPost(
            id = "planned_post_${MockClosifyData.outfitPosts.size + 1}",
            author = author,
            outfit = outfit.copy(ownerUserId = userId),
            title = title?.take(100)?.ifBlank { null },
            type = OutfitPostType.PLANNED,
            createdAt = createdAt,
            plannedDate = plannedDate
        )
        return outfitPostRepository.addPost(post)
    }

    suspend fun savePlanning(
        userId: String,
        title: String,
        garments: List<Garment>,
        plannedDate: String,
        createdAt: String,
        editingPostId: String?
    ): OutfitPost? {
        val outfit = Outfit(
            id = editingPostId?.let { "outfit_$it" } ?: "planned_outfit_${System.currentTimeMillis()}",
            garments = garments.distinctBy { it.id },
            ownerUserId = userId,
            name = title.ifBlank { null },
            createdAt = createdAt
        )

        return if (editingPostId == null) {
            savePlannedOutfitPost(
                userId = userId,
                title = title,
                outfit = outfit,
                plannedDate = plannedDate,
                createdAt = createdAt
            )
        } else {
            updatePlannedOutfitPost(
                postId = editingPostId,
                title = title,
                outfit = outfit,
                plannedDate = plannedDate
            )
        }
    }

    suspend fun updatePlannedOutfitPost(
        postId: String,
        title: String?,
        outfit: Outfit,
        plannedDate: String
    ): OutfitPost? {
        val currentPost = outfitPostRepository.getPost(postId) ?: return null
        val updatedPost = currentPost.copy(
            outfit = outfit.copy(ownerUserId = currentPost.author.id),
            title = title?.take(100)?.ifBlank { null },
            plannedDate = plannedDate
        )
        return outfitPostRepository.updatePost(updatedPost)
    }

    suspend fun deletePlannedOutfitPost(postId: String) {
        outfitPostRepository.deletePost(postId)
    }

    suspend fun getPlannedPostById(postId: String): OutfitPost? =
        outfitPostRepository.getPost(postId)?.takeIf { it.type == OutfitPostType.PLANNED }

    suspend fun getPlannedPostByDate(userId: String, plannedDate: String): OutfitPost? =
        getPlannedPosts(userId).firstOrNull { it.plannedDate == plannedDate }
}
