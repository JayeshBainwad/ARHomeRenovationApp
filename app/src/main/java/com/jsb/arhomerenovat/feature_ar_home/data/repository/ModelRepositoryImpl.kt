// ModelRepositoryImpl.kt
package com.jsb.arhomerenovat.feature_ar_home.data.repository

import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutDao
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDao
import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutEntity
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutWithModels
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val layoutDao: LayoutDao,
    private val modelDao: ModelDao
) : ModelRepository {

    override suspend fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>) {
        val layoutId = layoutDao.insertLayout(LayoutEntity(layoutName = layoutName)).toInt()
        val modelsWithLayoutId = models.map { it.copy(layoutId = layoutId) }
        modelDao.insertModels(modelsWithLayoutId)
    }

    override fun getAllLayouts(): Flow<List<LayoutWithModels>> {
        return modelDao.getAllLayoutsWithModels()
    }

    override suspend fun getModelsForLayout(layoutId: Int): List<ModelEntity> {
        return modelDao.getModelsForLayout(layoutId)
    }

    override suspend fun deleteLayout(layoutId: Int) {
        modelDao.deleteModelsForLayout(layoutId)
        modelDao.deleteLayout(layoutId)
    }
}