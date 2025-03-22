package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geo_model_table")
data class ModelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val modelName: String,
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val qx: Float,  // ✅ Quaternion X
    val qy: Float,  // ✅ Quaternion Y
    val qz: Float,  // ✅ Quaternion Z
    val qw: Float,  // ✅ Quaternion W
    val latitude: Double,
    val longitude: Double,
    val altitude: Double
)

