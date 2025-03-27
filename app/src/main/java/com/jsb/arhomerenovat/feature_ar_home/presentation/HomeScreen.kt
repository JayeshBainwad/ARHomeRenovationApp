package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.filament.Material
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData
import com.jsb.arhomerenovat.feature_ar_home.drawer.DrawerScreen
import com.jsb.arhomerenovat.feature_ar_home.drawer.MenuItem
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.getPngImageForModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
        ModelData(getPngImageForModel("White Couch.glb"), "White Couch.glb")
    )

//    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
        confirmStateChange = { true } // Add conditions if necessary
    )
    val itemsState = remember {
        mutableStateListOf(
            MenuItem(
                title = "3D Models",
                isSelected = true,
                iconResId = R.drawable.menu_book_, // ✅ Vector Asset in drawable
                description = "Display all 3D models"
            ),
            MenuItem(
                title = "Saved Layouts",
                iconResId = R.drawable.menu_book_, // ✅ Vector Asset in drawable
                description = "Display all saved layouts"
            ),
            MenuItem(
                title = "Profile",
                iconResId = R.drawable.profile, // ✅ Vector Asset in drawable
                description = "Open Profile Page"
            ),
        )
    }
    // Calculate offset based on drawer's state
    val drawerWidth = 320.dp
    val maxOffset = with(LocalDensity.current) { drawerWidth.toPx() }
    val drawerOffsetPx = drawerState.currentOffset + maxOffset
    val context = LocalContext.current

    ModalNavigationDrawer(
        modifier = modifier,
        gesturesEnabled = drawerState.isOpen,
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .padding(top = 44.dp)
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)),
            ) {
                Box(modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                    .height(40.dp)
                    .width(320.dp)
                )
                DrawerScreen(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    items = itemsState,
                    onClick = { menuItem ->

                        // Update selected menu state
                        itemsState.forEachIndexed { index, item ->
                            itemsState[index] = item.copy(
                                isSelected = item.id == menuItem.id
                            )
                        }

                        when (menuItem.title) {
                            "3D Models" -> {
                                Toast.makeText(
                                    context,
                                    "Open 3D Models Page",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("home_screen")
                            }
                            "Saved Layouts" -> {
                                Toast.makeText(
                                    context,
                                    "Open Saved Layouts Page",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("saved_layouts_screen")
                            } // ➡️ Create SavedLayoutsScreen
                            "Profile" -> {
                                Toast.makeText(
                                    context,
                                    "Open Profile Page",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("profile_screen")
                            } // ➡️ Create ProfileScreen
                        }

                        scope.launch {
                            delay(200)
                            drawerState.close()
                        }
                    }
                )
            }
        },
        content = {
            Scaffold(
                modifier = modifier
                    .graphicsLayer(
                        translationX = drawerOffsetPx,
                        alpha = if (drawerState.isOpen) 0.7f else 1f
                    ),
                snackbarHost = { SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = modifier
                ) },
                topBar = {
                    TopAppBar(
                        title = { Text("3D Models") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) // ✅ Correct Implementation
                            {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), // Grid with 3 columns
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(models) { model ->
                            ModelItem(model = model, onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                onModelSelected(model.modelFileName)
                            })
                        }
                    }
                }
            )
        }
    )


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