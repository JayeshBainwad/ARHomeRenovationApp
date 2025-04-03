package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ModelEntity.kt
@Entity(
    tableName = "models",
    foreignKeys = [ForeignKey(
        entity = LayoutEntity::class,
        parentColumns = ["layoutId"],
        childColumns = ["layoutId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val modelId: Int = 0, // ✅ Unique ID for model
    val layoutId: Int, // ✅ Foreign key linking to LayoutEntity
    val modelName: String, // ✅ Name of 3D model
    val posX: Float, val posY: Float, val posZ: Float, // ✅ Model Position
    val qx: Float, val qy: Float, val qz: Float, val qw: Float, // ✅ Rotation as Quaternion
    val scaleX: Float, val scaleY: Float, val scaleZ: Float, // ✅ Scale
    val latitude: Double, val longitude: Double, val altitude: Double // ✅ Geospatial Data
)