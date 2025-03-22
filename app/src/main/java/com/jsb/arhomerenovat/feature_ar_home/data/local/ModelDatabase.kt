package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ModelEntity::class],
    version = 1, // 🔥 Increment this to the latest version
    exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao
    companion object {
        const val DATABASE_NAME = "geo_model_db"
    }
}
