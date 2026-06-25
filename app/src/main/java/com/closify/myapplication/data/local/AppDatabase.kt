package com.closify.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.closify.myapplication.data.local.dao.FriendRequestDao
import com.closify.myapplication.data.local.dao.FriendshipDao
import com.closify.myapplication.data.local.dao.GarmentDao
import com.closify.myapplication.data.local.dao.NotificationDao
import com.closify.myapplication.data.local.dao.OutfitDao
import com.closify.myapplication.data.local.dao.OutfitPostDao
import com.closify.myapplication.data.local.dao.UserDao
import com.closify.myapplication.data.local.dao.WeatherDao
import com.closify.myapplication.data.local.entity.CommentEntity
import com.closify.myapplication.data.local.entity.FriendRequestEntity
import com.closify.myapplication.data.local.entity.FriendshipEntity
import com.closify.myapplication.data.local.entity.GarmentEntity
import com.closify.myapplication.data.local.entity.LikeEntity
import com.closify.myapplication.data.local.entity.NotificationEntity
import com.closify.myapplication.data.local.entity.OutfitEntity
import com.closify.myapplication.data.local.entity.OutfitPostEntity
import com.closify.myapplication.data.local.entity.UserEntity
import com.closify.myapplication.data.local.entity.WeatherCurrentEntity
import com.closify.myapplication.data.local.entity.WeatherForecastEntity

@Database(
    entities = [
        UserEntity::class,
        GarmentEntity::class,
        OutfitEntity::class,
        FriendshipEntity::class,
        FriendRequestEntity::class,
        NotificationEntity::class,
        OutfitPostEntity::class,
        LikeEntity::class,
        CommentEntity::class,
        WeatherCurrentEntity::class,
        WeatherForecastEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun garmentDao(): GarmentDao
    abstract fun outfitDao(): OutfitDao
    abstract fun friendshipDao(): FriendshipDao
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun outfitPostDao(): OutfitPostDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "closify_db"
                ).fallbackToDestructiveMigration(true).build().also { instance = it }
            }
    }
}
