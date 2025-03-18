package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import com.jsb.arhomerenovat.R

fun getPngImageForModel(modelFileName: String): Int {
    return when (modelFileName) {
        "android robot.glb" -> R.drawable.android_robot
        "Black Chair.glb" -> R.drawable.black_chair
        "White Chair.glb" -> R.drawable.white_chair
        "Gray Chair.glb" -> R.drawable.gray_chair
        "Brown Table 1.glb" -> R.drawable.brown_table_1
        "Brown Table 2.glb" -> R.drawable.brown_table_2
        "White Table.glb" -> R.drawable.white_table
        "Red Couch.glb" -> R.drawable.red_couch
        "Brown Couch.glb" -> R.drawable.brown_couch
        "White Couch.glb" -> R.drawable.white_couch
        else -> R.drawable.profile // A default fallback image
    }
}
