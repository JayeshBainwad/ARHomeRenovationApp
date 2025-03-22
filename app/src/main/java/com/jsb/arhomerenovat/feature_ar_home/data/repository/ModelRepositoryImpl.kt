package com.jsb.arhomerenovat.feature_ar_home.data.repository

import android.util.Log
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDao
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private const val TAG = "ARDepthScreen"

class ModelRepositoryImpl(private val dao: ModelDao) : ModelRepository {

    override suspend fun insertGeoModel(model: ModelEntity) {
        Log.d(TAG, "💾 Inserting model: $model")
        dao.insertGeoModel(model)

        val allModels = dao.getAllGeoModels().first() // Get data immediately
        Log.d(TAG, "📂 Models after insert: $allModels")
    }

    override fun getAllGeoModels(): Flow<List<ModelEntity>> = dao.getAllGeoModels()

    override suspend fun deleteGeoModel(model: ModelEntity) = dao.deleteGeoModel(model)

    override suspend fun clearAllGeoModels() = dao.clearAllGeoModels()
}


