package com.closify.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.closify.myapplication.data.local.dao.GarmentDao
import com.closify.myapplication.data.local.dao.OutfitDao
import com.closify.myapplication.data.local.dao.UserDao
import com.closify.myapplication.data.local.entity.GarmentEntity
import com.closify.myapplication.data.local.entity.OutfitEntity
import com.closify.myapplication.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, GarmentEntity::class, OutfitEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun garmentDao(): GarmentDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "closify_db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
