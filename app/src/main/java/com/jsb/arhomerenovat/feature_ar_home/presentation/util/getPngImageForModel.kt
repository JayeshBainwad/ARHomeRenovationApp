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
        "Ceiling Fan.glb" -> R.drawable.ceiling_fan
        "Ceiling fan (1).glb" -> R.drawable.ceiling_fan__1_
        "Ceiling Lamp.glb" -> R.drawable.ceiling_lamp
        "Ceiling Light (1).glb" -> R.drawable.ceiling_light__1_
        "Ceiling Light.glb" -> R.drawable.ceiling_light
        "Floor Tile.glb" -> R.drawable.floor_tile
        "Picnic Basket.glb" -> R.drawable.picnic_basket
        "Wall dsk speaker.glb" -> R.drawable.wall_desk_speakers
        else -> R.drawable.profile // A default fallback image
    }
}


