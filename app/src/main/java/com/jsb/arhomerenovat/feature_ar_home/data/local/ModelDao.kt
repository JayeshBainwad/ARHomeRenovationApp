// ModelDao.kt
package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelEntity>)

    @Transaction
    @Query("SELECT * FROM layouts")
    fun getAllLayoutsWithModels(): Flow<List<LayoutWithModels>>

    @Query("SELECT * FROM models WHERE layoutId = :layoutId")
    suspend fun getModelsForLayout(layoutId: Int): List<ModelEntity>

    @Query("DELETE FROM layouts WHERE layoutId = :layoutId")
    suspend fun deleteLayout(layoutId: Int)

    @Query("DELETE FROM models WHERE layoutId = :layoutId")
    suspend fun deleteModelsForLayout(layoutId: Int)
}

data class LayoutWithModels(
    @Embedded val layout: LayoutEntity,
    @Relation(
        parentColumn = "layoutId",
        entityColumn = "layoutId"
    )
    val models: List<ModelEntity>
)