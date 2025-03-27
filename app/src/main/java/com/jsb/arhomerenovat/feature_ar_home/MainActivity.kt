package com.jsb.arhomerenovat.feature_ar_home

import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationScreen
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsb.arhomerenovat.feature_ar_home.presentation.HomeScreen
import com.jsb.arhomerenovat.feature_ar_home.presentation.ProfileScreen
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

                NavHost(navController = navController, startDestination = "ar_depth_screen") {
                    composable("home_screen") {
                        HomeScreen(
                            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
                            navController
                        ) { selectedModel ->
                            navController.navigate("ar_depth_screen/$selectedModel")
                        }
                    }

                    composable("ar_depth_screen/{modelFileName}") { backStackEntry ->
                        val modelFileName = backStackEntry.arguments?.getString("modelFileName") ?: ""
                        ARDepthEstimationScreen(modelFileName)
                    }

//                    composable("saved_layouts_screen") {
//                        SavedLayoutsScreen(
//                            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
//                            navController = navController
//                        )
//                    }

                    composable("profile_screen") {
                        ProfileScreen()
                    }
                }

            }
        }
    }
}



//package com.jsb.arhomerenovat.feature_ar_home
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import com.jsb.arhomerenovat.feature_ar_home.presentation.ARCoreView
//import com.jsb.arhomerenovat.feature_ar_home.presentation.util.PermissionHandler
//import com.jsb.arhomerenovat.ui.theme.ARHomeRenovatTheme
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    private val requestPermissionsLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            val allGranted = permissions.all { it.value }
//            if (allGranted) {
//                Log.d("Permissions", "✅ All permissions granted")
//                PermissionHandler.promptEnableLocation(this)
//            } else {
//                Log.e("Permissions", "❌ Permissions denied")
//                Toast.makeText(
//                    this,
//                    "Location & Camera permissions are required for AR features.",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        PermissionHandler.requestPermissions(this, requestPermissionsLauncher)
//
//        setContent {
//            ARHomeRenovatTheme {
//                ARCoreView()
//            }
//        }
//    }
//}