package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LayoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayout(layout: LayoutEntity): Long // Returns the ID of the inserted layout

    @Query("SELECT * FROM layouts ORDER BY timestamp DESC")
    suspend fun getAllLayouts(): List<LayoutEntity>
}