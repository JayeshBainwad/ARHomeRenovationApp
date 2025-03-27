package com.jsb.arhomerenovat.feature_ar_home.data.repository

import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutDao
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDao
import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutEntity
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val layoutDao: LayoutDao,
    private val modelDao: ModelDao
) : ModelRepository {

    override suspend fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>) {
        val layoutId = layoutDao.insertLayout(LayoutEntity(layoutName = layoutName)).toInt() // ✅ Convert to String
        val modelsWithLayoutId = models.map { it.copy(layoutId = layoutId) }
        modelDao.insertModels(modelsWithLayoutId)
    }

    override suspend fun getAllLayouts(): List<LayoutEntity> = layoutDao.getAllLayouts()

    override suspend fun getModelsByLayout(layoutId: Int): List<ModelEntity> = modelDao.getModelsByLayout(layoutId)
}
