package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.*

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelEntity>)

    @Query("SELECT * FROM models WHERE layoutId = :layoutId")
    suspend fun getModelsByLayout(layoutId: Int): List<ModelEntity>
}
