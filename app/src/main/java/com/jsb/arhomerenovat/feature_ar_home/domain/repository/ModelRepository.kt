package com.jsb.arhomerenovat.feature_ar_home.domain.repository

import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun insertGeoModel(model: ModelEntity)
    fun getAllGeoModels(): Flow<List<ModelEntity>>  // ✅ Return Flow
    suspend fun deleteGeoModel(model: ModelEntity)
    suspend fun clearAllGeoModels()
}

