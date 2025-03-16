package com.jsb.arhomerenovat.feature_ar_home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

//            ARCoreView()
//            ARScreen1()
//            val context = LocalContext.current
//            ARCoreDepthView(context = context)
//            ARDepthEstimationScreen()

            val navController = rememberNavController()

            NavHost(navController, startDestination = "home_screen") {
                composable("home_screen") {
                    HomeScreen(navController) { selectedModel ->
                        navController.navigate("ar_depth_screen/$selectedModel")
                    }
                }

                composable("ar_depth_screen/{modelFileName}") { backStackEntry ->
                    val modelFileName = backStackEntry.arguments?.getString("modelFileName") ?: ""
                    ARDepthEstimationScreen(modelFileName)
                }
            }
        }
    }
}