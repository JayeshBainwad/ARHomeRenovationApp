// LayoutDao.kt
package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LayoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayout(layout: LayoutEntity): Long

    @Query("SELECT * FROM layouts WHERE layoutId = :layoutId")
    suspend fun getLayoutById(layoutId: Int): LayoutEntity?
}