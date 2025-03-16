package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jsb.arhomerenovat.R

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 3D Model Preview
        val modelPreview: Painter = painterResource(id = R.drawable.android_robot)
        Image(
            painter = modelPreview,
            contentDescription = "3D Model Preview",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .shadow(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Preview Button
        Button(
            onClick = { navController.navigate("ar_depth_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Preview in AR")
        }
    }
}
