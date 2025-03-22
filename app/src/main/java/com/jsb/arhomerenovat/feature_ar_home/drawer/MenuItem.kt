package com.jsb.arhomerenovat.feature_ar_home.drawer

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: String = "",
    val title: String = "",
    val isSelected: Boolean = false,
    @DrawableRes val iconResId: Int? = null, // 🔄 For Vector Asset in drawable
    val description: String = ""
)

