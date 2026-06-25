package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.closify.myapplication.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE receiverId = :userId ORDER BY createdAt DESC")
    fun observeByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE receiverId = :userId AND read = 0")
    fun observeUnreadCount(userId: String): Flow<Int>

    @Query("SELECT * FROM notifications WHERE receiverId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUserId(userId: String): List<NotificationEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE receiverId = :userId AND read = 0")
    suspend fun getUnreadCount(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET read = 1 WHERE receiverId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}
