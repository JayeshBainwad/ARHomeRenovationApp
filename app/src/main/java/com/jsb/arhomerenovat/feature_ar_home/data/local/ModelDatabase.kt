// ModelDatabase.kt
package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LayoutEntity::class, ModelEntity::class],
    version = 3,
    exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {
    abstract fun layoutDao(): LayoutDao
    abstract fun modelDao(): ModelDao

    companion object {
        const val DATABASE_NAME = "ar_home_db"
    }
}