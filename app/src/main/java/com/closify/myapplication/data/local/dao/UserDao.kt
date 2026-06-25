package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.closify.myapplication.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<UserEntity>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)
}
