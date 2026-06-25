package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.closify.myapplication.data.local.entity.FriendRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRequestDao {
    @Query("SELECT * FROM friend_requests WHERE senderId = :userId OR receiverId = :userId")
    fun observeAllByUserId(userId: String): Flow<List<FriendRequestEntity>>

    @Query("SELECT * FROM friend_requests WHERE receiverId = :userId AND status = 'PENDING'")
    fun observePendingIncoming(userId: String): Flow<List<FriendRequestEntity>>

    @Query("SELECT * FROM friend_requests WHERE id = :requestId")
    suspend fun getById(requestId: String): FriendRequestEntity?

    @Query("SELECT * FROM friend_requests WHERE senderId = :userId OR receiverId = :userId")
    suspend fun getAllByUserId(userId: String): List<FriendRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(request: FriendRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(requests: List<FriendRequestEntity>)

    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteById(requestId: String)

    @Query("DELETE FROM friend_requests")
    suspend fun deleteAll()
}
