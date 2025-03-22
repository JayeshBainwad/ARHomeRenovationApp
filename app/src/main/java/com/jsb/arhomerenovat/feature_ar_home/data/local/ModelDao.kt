package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeoModel(model: ModelEntity)

    @Query("SELECT * FROM geo_model_table")
    fun getAllGeoModels(): Flow<List<ModelEntity>>  // ✅ Return Flow

    @Delete
    suspend fun deleteGeoModel(model: ModelEntity)

    @Query("DELETE FROM geo_model_table")
    suspend fun clearAllGeoModels()
}

