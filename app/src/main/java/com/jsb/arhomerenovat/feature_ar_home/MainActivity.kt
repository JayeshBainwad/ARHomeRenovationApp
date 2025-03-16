package com.jsb.arhomerenovat.feature_ar_home

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.HomeScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.ProfileScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.SavedLayoutsScreen
import com.jsb.arhomerenovat.ui.theme.ARHomeRenovatTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARHomeRenovatTheme { // ✅ Wrap your UI in the theme
                val navController = rememberNavController()
                val modifier = Modifier

                NavHost(navController, startDestination = "home_screen") {

                    composable("home_screen") {
                        HomeScreen(modifier.background(color = MaterialTheme.colorScheme.background), navController) { selectedModel ->
                            navController.navigate("ar_depth_screen/$selectedModel")
                        }
                    }

                    composable("ar_depth_screen/{modelFileName}") { backStackEntry ->
                        val modelFileName = backStackEntry.arguments?.getString("modelFileName") ?: ""
                        ARDepthEstimationScreen(modelFileName)
                    }

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
}
