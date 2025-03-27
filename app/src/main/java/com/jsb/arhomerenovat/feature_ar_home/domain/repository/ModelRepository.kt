package com.jsb.arhomerenovat.feature_ar_home.domain.repository

import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutEntity
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity

interface ModelRepository {
    suspend fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>)
    suspend fun getAllLayouts(): List<LayoutEntity>
    suspend fun getModelsByLayout(layoutId: Int): List<ModelEntity> // ✅ Ensure String type
}
