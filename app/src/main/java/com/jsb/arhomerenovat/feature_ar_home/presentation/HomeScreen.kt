package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, onModelSelected: (String) -> Unit) {
    val models = listOf(
        ModelData(R.drawable.android_robot, "android robot.glb"),
        ModelData(R.drawable.black_chair, "Black Chair.glb"),
        ModelData(R.drawable.white_chair, "White Chair.glb"),
        ModelData(R.drawable.gray_chair, "Gray Chair.glb"),
        ModelData(R.drawable.brown_table_1, "Brown Table 1.glb"),
        ModelData(R.drawable.brown_table_2, "Brown Table 2.glb"),
        ModelData(R.drawable.white_table, "White Table.glb"),
        ModelData(R.drawable.red_couch, "Red Couch.glb"),
        ModelData(R.drawable.brown_couch, "Brown Couch.glb"),
        ModelData(R.drawable.white_couch, "White Couch.glb")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("3D Models") },
                navigationIcon = {
                    IconButton(onClick = { /* Open Drawer Logic */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Grid with 3 columns
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(models) { model ->
                ModelItem(model = model, onClick = { onModelSelected(model.modelFileName) })
            }
        }
    }
}

@Composable
fun ModelItem(model: ModelData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = model.imageResId),
            contentDescription = "3D Model Preview",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(8.dp)
        )
    }
}

