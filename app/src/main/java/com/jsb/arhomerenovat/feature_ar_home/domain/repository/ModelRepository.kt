// ModelRepository.kt
package com.jsb.arhomerenovat.feature_ar_home.domain.repository

import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutWithModels
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>)
    fun getAllLayouts(): Flow<List<LayoutWithModels>>
    suspend fun getModelsForLayout(layoutId: Int): List<ModelEntity>
    suspend fun deleteLayout(layoutId: Int)
}