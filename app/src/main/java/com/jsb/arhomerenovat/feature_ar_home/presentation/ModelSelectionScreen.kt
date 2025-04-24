package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.getPngImageForModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onModelSelected: (String) -> Unit
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select 3D Model",
                        color = colorResource(R.color.top_app_bar_text),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(R.color.top_app_bar_text) // Set icon color
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = colorResource(R.color.top_app_bar_text),
                    navigationIconContentColor = colorResource(R.color.top_app_bar_text),
                    actionIconContentColor = colorResource(R.color.top_app_bar_text)
                )
            )
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp) // Add bottom padding
            ) {
                items(models) { model ->
                    ModelItem(model = model, onClick = {
                        onModelSelected(model.modelFileName)
                    })
                }
            }
        }
    )
}

@Composable
fun ModelItem(model: ModelData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 14.dp )
            .fillMaxWidth()
            .aspectRatio(0.8f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 32.dp, // Shadow size
            pressedElevation = 12.dp, // Shadow when pressed
            focusedElevation = 15.dp, // Shadow when focused
            hoveredElevation = 18.dp // Shadow when hovered
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .background(color = colorResource(id = R.color.card_background))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = model.imageResId),
                contentDescription = "3D Model Preview",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(color = Color.Transparent)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit // Add content scale
            )
            Text(
                text = model.modelFileName.removeSuffix(".glb"),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}