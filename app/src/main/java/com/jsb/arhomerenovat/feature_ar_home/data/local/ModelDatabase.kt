package com.jsb.arhomerenovat.feature_ar_home.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LayoutEntity::class, ModelEntity::class], // ✅ Include both entities
    version = 2, // 🔥 Increment version for new changes
    exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {
    abstract fun layoutDao(): LayoutDao
    abstract fun modelDao(): ModelDao

    companion object {
        const val DATABASE_NAME = "ar_home_db"

        @Volatile
        private var INSTANCE: ModelDatabase? = null

        fun getDatabase(context: Context): ModelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ModelDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // ✅ Handles version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

