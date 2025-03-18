package com.jsb.arhomerenovat.feature_ar_home.domain

import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation

// Model data class to link PNG previews with 3D model files
data class ModelData(
    val imageResId: Int,            // 🔹 For displaying model preview
    val modelFileName: String,      // 🔹 For loading the actual 3D model
    val position: Position? = null, // 🔹 For saving model's real-world position
    val rotation: Rotation? = null  // 🔹 For saving model's rotation
)