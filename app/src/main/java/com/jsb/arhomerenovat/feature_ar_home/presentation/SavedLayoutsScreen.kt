package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SavedLayoutsScreen(viewModel: ARDepthEstimationViewModel = hiltViewModel()) {
    Box {
        Text(text = "Saved Layouts")
    }
}
