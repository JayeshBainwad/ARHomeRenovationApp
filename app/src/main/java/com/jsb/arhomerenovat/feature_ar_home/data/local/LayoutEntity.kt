package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// LayoutEntity.kt
@Entity(tableName = "layouts")
data class LayoutEntity(
    @PrimaryKey(autoGenerate = true) val layoutId: Int = 0,
    val layoutName: String,
    val createdAt: Long = System.currentTimeMillis()
)