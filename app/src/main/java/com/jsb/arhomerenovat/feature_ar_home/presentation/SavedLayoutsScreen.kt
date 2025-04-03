// SavedLayoutScreen.kt
package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jsb.arhomerenovat.feature_ar_home.presentation.components.LayoutListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLayoutScreen(
    navController: NavController,
    viewModel: ARDepthEstimationViewModel = hiltViewModel()
) {
    val layouts by viewModel.savedLayouts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllLayouts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Layouts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (layouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved layouts found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    items(layouts) { layoutWithModels ->
                        LayoutListItem(
                            layout = layoutWithModels.layout,
                            modelCount = layoutWithModels.models.size,
                            onClick = {
                                navController.navigate("ar_screen/${layoutWithModels.layout.layoutId}")
                            },
                            onDelete = {
                                viewModel.deleteLayout(layoutWithModels.layout.layoutId)
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    )
}