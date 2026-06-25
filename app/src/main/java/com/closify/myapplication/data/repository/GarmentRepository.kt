package com.closify.myapplication.data.repository

import android.content.Context
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toGarmentEntity
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

data class PlannerGarmentGroups(
    val topAndOuterwear: List<Garment>,
    val bottoms: List<Garment>,
    val footwear: List<Garment>,
    val fullBody: List<Garment>
)

class GarmentRepository private constructor(context: Context) {

    companion object {
        @Volatile private var _instance: GarmentRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = GarmentRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: GarmentRepository
            get() = _instance ?: error("GarmentRepository.initialize(context) no fue llamado.")
    }

    private val garmentDao = AppDatabase.getInstance(context).garmentDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeGarments(userId: String): Flow<List<Garment>> =
        garmentDao.observeByUserId(userId)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun syncFromFirestore(userId: String) {
        try {
            val snapshot = firestore.collection("users/$userId/garments").get().await()
            val entities = snapshot.documents.mapNotNull { it.toGarmentEntity() }
            
            if (entities.isEmpty()) {
                garmentDao.deleteAllByUserId(userId)
            } else {
                garmentDao.upsertAll(entities)
                garmentDao.deleteNotInList(userId, entities.map { it.id })
            }
        } catch (e: Exception) {
            // Sin conexión — Room ya tiene los datos del último sync
        }
    }

    suspend fun createGarment(
        ownerUserId: String,
        name: String,
        category: GarmentCategory,
        imageUrl: String,
        suitableWeather: Set<WeatherCondition>,
        suitableOccasions: Set<Occasion>
    ): Garment {
        val garment = Garment(
            id = UUID.randomUUID().toString(),
            ownerUserId = ownerUserId,
            name = name.trim(),
            category = category,
            imageUrl = imageUrl,
            suitableWeather = suitableWeather.ifEmpty { setOf(WeatherCondition.ANY) },
            suitableOccasions = suitableOccasions.ifEmpty { setOf(Occasion.ANY) },
            createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
            )
        )
        garmentDao.upsert(garment.toEntity())
        scope.launch {
            firestore.collection("users/$ownerUserId/garments")
                .document(garment.id)
                .set(garment.toFirestoreMap())
                .await()
        }
        return garment
    }

    suspend fun getAllByUserId(userId: String): List<Garment> =
        garmentDao.getAllByUserId(userId).map { it.toDomain() }

    suspend fun getByCategory(category: GarmentCategory, userId: String): List<Garment> =
        getAllByUserId(userId).filter { it.category == category }

    suspend fun getByOccasion(occasion: Occasion, userId: String): List<Garment> =
        getAllByUserId(userId).filter { occasion in it.suitableOccasions || Occasion.ANY in it.suitableOccasions }

    suspend fun getByWeather(weather: WeatherCondition, userId: String): List<Garment> =
        getAllByUserId(userId).filter { weather in it.suitableWeather || WeatherCondition.ANY in it.suitableWeather }

    suspend fun getById(id: String): Garment? =
        garmentDao.getById(id)?.toDomain()

    suspend fun deleteGarment(id: String, userId: String) {
        garmentDao.delete(id, userId)
        scope.launch {
            firestore.collection("users/$userId/garments").document(id).delete().await()
        }
    }

    suspend fun getPlannerGroups(userId: String): PlannerGarmentGroups {
        val garments = getAllByUserId(userId)
        return PlannerGarmentGroups(
            topAndOuterwear = garments.filter {
                it.category == GarmentCategory.TOP || it.category == GarmentCategory.OUTERWEAR
            },
            bottoms = garments.filter { it.category == GarmentCategory.BOTTOM },
            footwear = garments.filter { it.category == GarmentCategory.FOOTWEAR },
            fullBody = garments.filter { it.category == GarmentCategory.FULL_BODY }
        )
    }

    suspend fun searchByName(query: String, userId: String): List<Garment> {
        val cleanQuery = query.trim()
        return if (cleanQuery.isEmpty()) getAllByUserId(userId)
        else getAllByUserId(userId).filter { it.name.contains(cleanQuery, ignoreCase = true) }
    }

    suspend fun getCategoryCounts(userId: String): Map<GarmentCategory, Int> =
        getAllByUserId(userId).groupBy { it.category }.mapValues { it.value.size }

    suspend fun getWeatherCounts(userId: String): Map<WeatherCondition, Int> =
        WeatherCondition.entries.associateWith { getByWeather(it, userId).size }

    suspend fun getOccasionCounts(userId: String): Map<Occasion, Int> =
        Occasion.entries.associateWith { getByOccasion(it, userId).size }
}
