package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.closify.myapplication.data.local.entity.GarmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GarmentDao {

    @Query("SELECT * FROM garments WHERE ownerUserId = :userId")
    fun observeByUserId(userId: String): Flow<List<GarmentEntity>>

    @Query("SELECT * FROM garments WHERE ownerUserId = :userId")
    suspend fun getAllByUserId(userId: String): List<GarmentEntity>

    @Query("SELECT * FROM garments WHERE id = :id")
    suspend fun getById(id: String): GarmentEntity?

    @Upsert
    suspend fun upsert(garment: GarmentEntity)

    @Upsert
    suspend fun upsertAll(garments: List<GarmentEntity>)

    @Query("DELETE FROM garments WHERE id = :id AND ownerUserId = :userId")
    suspend fun delete(id: String, userId: String)
}
