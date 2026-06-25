package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.closify.myapplication.data.local.entity.FriendshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendshipDao {
    @Query("SELECT * FROM friendships WHERE userAId = :userId OR userBId = :userId")
    fun observeByUserId(userId: String): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE userAId = :userId OR userBId = :userId")
    suspend fun getAllByUserId(userId: String): List<FriendshipEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(friendship: FriendshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(friendships: List<FriendshipEntity>)

    @Query("DELETE FROM friendships WHERE (userAId = :userId AND userBId = :friendId) OR (userAId = :friendId AND userBId = :userId)")
    suspend fun deleteFriendship(userId: String, friendId: String)

    @Query("DELETE FROM friendships")
    suspend fun deleteAll()
}
