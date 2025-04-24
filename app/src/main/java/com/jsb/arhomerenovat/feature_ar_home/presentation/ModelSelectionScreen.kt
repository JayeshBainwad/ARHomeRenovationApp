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
        // --- Chairs ---
        ModelData(getPngImageForModel("Black Chair.glb"), "Black Chair.glb"),
        ModelData(getPngImageForModel("Chair.glb"), "Chair.glb"),
        ModelData(getPngImageForModel("Chair 1.glb"), "Chair 1.glb"),
        ModelData(getPngImageForModel("Chair 2.glb"), "Chair 2.glb"),
        ModelData(getPngImageForModel("Chair 4.glb"), "Chair 4.glb"),
        ModelData(getPngImageForModel("ChairC.glb"), "ChairC.glb"),
        ModelData(getPngImageForModel("Gray Chair.glb"), "Gray Chair.glb"),
        ModelData(getPngImageForModel("White Chair.glb"), "White Chair.glb"),
        ModelData(getPngImageForModel("Executive Chair.glb"), "Executive Chair.glb"),
        ModelData(getPngImageForModel("Office Chair.glb"), "Office Chair.glb"),
        ModelData(getPngImageForModel("Office Chair 2.glb"), "Office Chair 2.glb"),
        ModelData(getPngImageForModel("Armchair.glb"), "Armchair.glb"),

        // --- Sofas / Couches ---
        ModelData(getPngImageForModel("Brown Couch.glb"), "Brown Couch.glb"),
        ModelData(getPngImageForModel("Couch.glb"), "Couch.glb"),
        ModelData(getPngImageForModel("Couch 2.glb"), "Couch 2.glb"),
        ModelData(getPngImageForModel("Couch Brown.glb"), "Couch Brown.glb"),
        ModelData(getPngImageForModel("Couch Small.glb"), "Couch Small.glb"),
        ModelData(getPngImageForModel("Couch Wide.glb"), "Couch Wide.glb"),
        ModelData(getPngImageForModel("Red Couch.glb"), "Red Couch.glb"),
        ModelData(getPngImageForModel("White Couch.glb"), "White Couch.glb"),
        ModelData(getPngImageForModel("Sofa.glb"), "Sofa.glb"),
        ModelData(getPngImageForModel("Sofaa.glb"), "Sofaa.glb"),
        ModelData(getPngImageForModel("Lounge Design Sofa Corn.glb"), "Lounge Design Sofa Corn.glb"),
        ModelData(getPngImageForModel("Lounge Sofa Corner.glb"), "Lounge Sofa Corner.glb"),
        ModelData(getPngImageForModel("Lounge Sofa Ottoman.glb"), "Lounge Sofa Ottoman.glb"),
        ModelData(getPngImageForModel("big Couch.glb"), "big Couch.glb"),

        // --- Beds ---
        ModelData(getPngImageForModel("Bed.glb"), "Bed.glb"),
        ModelData(getPngImageForModel("Bed 3.glb"), "Bed 3.glb"),
        ModelData(getPngImageForModel("Bed Single.glb"), "Bed Single.glb"),
        ModelData(getPngImageForModel("Bed Twin.glb"), "Bed Twin.glb"),
        ModelData(getPngImageForModel("Bed Double.glb"), "Bed Double.glb"),
        ModelData(getPngImageForModel("Bed Double 2.glb"), "Bed Double 2.glb"),
        ModelData(getPngImageForModel("Bed King.glb"), "Bed King.glb"),

        // --- Tables / Desks / Dining ---
        ModelData(getPngImageForModel("Brown Table 1.glb"), "Brown Table 1.glb"),
        ModelData(getPngImageForModel("Brown Table 2.glb"), "Brown Table 2.glb"),
        ModelData(getPngImageForModel("White Table.glb"), "White Table.glb"),
        ModelData(getPngImageForModel("Table.glb"), "Table.glb"),
        ModelData(getPngImageForModel("Table and Chairs.glb"), "Table and Chairs.glb"),
        ModelData(getPngImageForModel("Desk.glb"), "Desk.glb"),
        ModelData(getPngImageForModel("Desk 2.glb"), "Desk 2.glb"),
        ModelData(getPngImageForModel("Dining Set.glb"), "Dining Set.glb"),
        ModelData(getPngImageForModel("Dining Set 2.glb"), "Dining Set 2.glb"),

        // --- Bookcases / Shelves ---
        ModelData(getPngImageForModel("Bookcase with Books.glb"), "Bookcase with Books.glb"),
        ModelData(getPngImageForModel("Bookshelf 4.glb"), "Bookshelf 4.glb"),
        ModelData(getPngImageForModel("Bookshelf big.glb"), "Bookshelf big.glb"),
        ModelData(getPngImageForModel("wooden bookshelf 3.glb"), "wooden bookshelf 3.glb"),
        ModelData(getPngImageForModel("zig zag bookshelf.glb"), "zig zag bookshelf.glb"),
        ModelData(getPngImageForModel("Wall Shelf.glb"), "Wall Shelf.glb"),
        ModelData(getPngImageForModel("Shelf Small.glb"), "Shelf Small.glb"),
        ModelData(getPngImageForModel("shelf 2.glb"), "shelf 2.glb"),

        // --- Kitchen Appliances ---
        ModelData(getPngImageForModel("Kitchen Cabinet.glb"), "Kitchen Cabinet.glb"),
        ModelData(getPngImageForModel("Kitchen Fridge.glb"), "Kitchen Fridge.glb"),
        ModelData(getPngImageForModel("Kitchen Fridge Large.glb"), "Kitchen Fridge Large.glb"),
        ModelData(getPngImageForModel("Kitchen Stove.glb"), "Kitchen Stove.glb"),
        ModelData(getPngImageForModel("Oven.glb"), "Oven.glb"),

        // --- Lights / Ceiling Fixtures ---
        ModelData(getPngImageForModel("Ceiling Fan.glb"), "Ceiling Fan.glb"),
        ModelData(getPngImageForModel("Ceiling fan (1).glb"), "Ceiling fan (1).glb"),
        ModelData(getPngImageForModel("Ceiling fan 2.glb"), "Ceiling fan 2.glb"),
        ModelData(getPngImageForModel("Fan.glb"), "Fan.glb"),
        ModelData(getPngImageForModel("Ceiling Lamp.glb"), "Ceiling Lamp.glb"),
        ModelData(getPngImageForModel("Ceiling Lamp 2.glb"), "Ceiling Lamp 2.glb"),
        ModelData(getPngImageForModel("Ceiling Light.glb"), "Ceiling Light.glb"),
        ModelData(getPngImageForModel("Ceiling Light (1).glb"), "Ceiling Light (1).glb"),
        ModelData(getPngImageForModel("Ceiling Light 2.glb"), "Ceiling Light 2.glb"),
        ModelData(getPngImageForModel("Ceiling Light Fixture.glb"), "Ceiling Light Fixture.glb"),
        ModelData(getPngImageForModel("Light Ceiling.glb"), "Light Ceiling.glb"),
        ModelData(getPngImageForModel("Light Ceiling Single.glb"), "Light Ceiling Single.glb"),
        ModelData(getPngImageForModel("Light Chandelier.glb"), "Light Chandelier.glb"),
        ModelData(getPngImageForModel("Chandelier.glb"), "Chandelier.glb"),

        // --- Plants / Decor ---
        ModelData(getPngImageForModel("Flower Pot.glb"), "Flower Pot.glb"),
        ModelData(getPngImageForModel("Flower Pot 2.glb"), "Flower Pot 2.glb"),
        ModelData(getPngImageForModel("Pot Plant.glb"), "Pot Plant.glb"),
        ModelData(getPngImageForModel("Houseplant.glb"), "Houseplant.glb"),
        ModelData(getPngImageForModel("Plants - Assorted shelf plants.glb"), "Plants - Assorted shelf plants.glb"),

        // --- Others / Miscellaneous ---
        ModelData(getPngImageForModel("Closet.glb"), "Closet.glb"),
        ModelData(getPngImageForModel("Picnic Basket.glb"), "Picnic Basket.glb"),
        ModelData(getPngImageForModel("Wall desk speakers.glb"), "Wall desk speakers.glb"),
        ModelData(getPngImageForModel("Room space test.glb"), "Room space test.glb"),
        ModelData(getPngImageForModel("android robot.glb"), "android robot.glb")
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