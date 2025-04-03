package com.jsb.arhomerenovat.feature_ar_home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutListItem(
    layout: LayoutEntity,
    modelCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = colorResource(id = R.color.card_background))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = layout.layoutName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = "Contains $modelCount models",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Layout",
                    tint = Color.White
                )
            }
        }
    }
}