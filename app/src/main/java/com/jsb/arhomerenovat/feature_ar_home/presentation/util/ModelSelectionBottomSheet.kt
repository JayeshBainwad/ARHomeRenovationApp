package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionBottomSheet(
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val models = listOf(
        ModelData(getPngImageForModel("android robot.glb"), "android robot.glb"),
        ModelData(getPngImageForModel("Black Chair.glb"), "Black Chair.glb"),
        ModelData(getPngImageForModel("White Chair.glb"), "White Chair.glb"),
        ModelData(getPngImageForModel("Gray Chair.glb"), "Gray Chair.glb"),
        ModelData(getPngImageForModel("Brown Table 1.glb"), "Brown Table 1.glb"),
        ModelData(getPngImageForModel("Brown Table 2.glb"), "Brown Table 2.glb"),
        ModelData(getPngImageForModel("White Table.glb"), "White Table.glb"),
        ModelData(getPngImageForModel("Red Couch.glb"), "Red Couch.glb"),
        ModelData(getPngImageForModel("Brown Couch.glb"), "Brown Couch.glb"),
        ModelData(getPngImageForModel("White Couch.glb"), "White Couch.glb"),
        ModelData(getPngImageForModel("Ceiling Fan.glb"), "Ceiling Fan.glb"),
        ModelData(getPngImageForModel("Ceiling fan (1).glb"), "Ceiling fan (1).glb"),
        ModelData(getPngImageForModel("Ceiling Lamp.glb"), "Ceiling Lamp.glb"),
        ModelData(getPngImageForModel("Ceiling Light (1).glb"), "Ceiling Light (1).glb"),
        ModelData(getPngImageForModel("Ceiling Light.glb"), "Ceiling Light.glb"),
        ModelData(getPngImageForModel("Floor Tile.glb"), "Floor Tile.glb"),
        ModelData(getPngImageForModel("Picnic Basket.glb"), "Picnic Basket.glb"),
        ModelData(getPngImageForModel("Wall dsk speaker.glb"), "Wall dsk speaker.glb")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select a 3D Model", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))

            models.forEach { model ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onModelSelected(model.modelFileName) }
                ) {
                    Icon(
                        painter = painterResource(id = model.imageResId),
                        contentDescription = model.modelFileName,
                        modifier = Modifier.size(40.dp)
                            .clickable { onModelSelected(model.modelFileName)}
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}