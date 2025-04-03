package com.jsb.arhomerenovat.feature_ar_home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.ModelSelectionScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.ProfileScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.SavedLayoutScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.PermissionHandler
import com.jsb.arhomerenovat.ui.theme.ARHomeRenovatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("Permissions", "✅ All permissions granted")
                PermissionHandler.promptEnableLocation(this)
            } else {
                Log.e("Permissions", "❌ Permissions denied")
                Toast.makeText(
                    this,
                    "Location & Camera permissions are required for AR features.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionHandler.requestPermissions(this, requestPermissionsLauncher)

        setContent {
            ARHomeRenovatTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home_screen"
                ) {
                    composable("home_screen") {
                        HomeScreen(navController = navController)
                    }

                    composable("model_selection_screen") {
                        ModelSelectionScreen(
                            modifier = Modifier.background(color = Color.White),
                            navController = navController,
                            onModelSelected = { selectedModel ->
                                navController.navigate("ar_depth_screen/$selectedModel")
                            }
                        )
                    }

                    composable("ar_depth_screen/{modelFileName}") { backStackEntry ->
                        val modelFileName = backStackEntry.arguments?.getString("modelFileName") ?: ""
                        ARDepthEstimationScreen(
                            initialModelFileName = modelFileName,
                            navigate = navController
                        )
                    }

                    composable("saved_layouts_screen") {
                        SavedLayoutScreen(navController = navController)
                    }

                    composable("profile_screen") {
                        ProfileScreen()
                    }

                    // Add this new route for loading saved layouts in AR
                    composable("ar_screen/{layoutId}") { backStackEntry ->
                        val layoutId = backStackEntry.arguments?.getString("layoutId")?.toIntOrNull() ?: run {
                            // Handle invalid layout ID
                            navController.popBackStack()
                            return@composable
                        }
                        ARDepthEstimationScreen(
                            layoutId = layoutId,
                            navigate = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "Let's Get Started" text at top center
        Text(
            text = "Let's Get Started",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.lets_get_started),
            modifier = Modifier.padding(top = 62.dp)
        )

        // Card container
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 3D Models Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .background(color = Color.Transparent)
                    .clickable { navController.navigate("model_selection_screen") },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.card_background)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "3D Models",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }

            // Saved Layouts Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .background(color = Color.Transparent)
                    .clickable { navController.navigate("saved_layouts_screen") },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.card_background)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Saved Layouts",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}