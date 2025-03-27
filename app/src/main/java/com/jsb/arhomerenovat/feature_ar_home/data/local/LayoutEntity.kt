package com.jsb.arhomerenovat.feature_ar_home.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layouts")
data class LayoutEntity(
    @PrimaryKey(autoGenerate = true) val layoutId: Int = 0, // Auto-generated unique ID
    val timestamp: Long = System.currentTimeMillis(), // Save timestamp
    val layoutName: String // Layout name (optional)
)