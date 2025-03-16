package com.jsb.arhomerenovat.feature_ar_home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.HomeScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.ProfileScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.SavedLayoutsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            val modifier = Modifier

            NavHost(navController, startDestination = "home_screen") {

                composable("home_screen") {
                    HomeScreen(modifier, navController) { selectedModel ->
                        navController.navigate("ar_depth_screen/$selectedModel")
                    }
                }

                composable("ar_depth_screen/{modelFileName}") { backStackEntry ->
                    val modelFileName = backStackEntry.arguments?.getString("modelFileName") ?: ""
                    ARDepthEstimationScreen(modelFileName)
                }

                // ➡️ Added Navigation for Drawer Items
                composable("saved_layouts_screen") {
                    SavedLayoutsScreen()
                }

                composable("profile_screen") {
                    ProfileScreen()
                }
            }
        }
    }
}
