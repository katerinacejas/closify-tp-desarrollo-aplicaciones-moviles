package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.closify.myapplication.data.local.entity.OutfitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {

    @Query("SELECT * FROM outfits WHERE ownerUserId = :userId")
    fun observeByUserId(userId: String): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE ownerUserId = :userId")
    suspend fun getAllByUserId(userId: String): List<OutfitEntity>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getById(id: String): OutfitEntity?

    @Upsert
    suspend fun upsert(outfit: OutfitEntity)

    @Upsert
    suspend fun upsertAll(outfits: List<OutfitEntity>)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM outfits WHERE ownerUserId = :userId AND id NOT IN (:remainingIds)")
    suspend fun deleteNotInList(userId: String, remainingIds: List<String>)

    @Query("DELETE FROM outfits WHERE ownerUserId = :userId")
    suspend fun deleteAllByUserId(userId: String)
}
