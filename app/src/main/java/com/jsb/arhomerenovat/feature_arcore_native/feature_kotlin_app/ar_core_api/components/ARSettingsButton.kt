package com.jsb.arhomerenovat.feature_arcore_native.feature_kotlin_app.ar_core_api.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.DepthSettings
import com.jsb.arhomerenovat.feature_arcore_native.feature_kotlin_app.ar_core_api.HelloArActivity

@Composable
fun ARSettingsButton(
    activity: HelloArActivity,
    depthSettings: DepthSettings
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    val session = activity.arCoreSessionHelper.session

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { showSettingsDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_button_content_description)
            )
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(R.string.options_title_with_depth)) },
            text = {
                Column {
                    if (session?.isDepthModeSupported(Config.DepthMode.AUTOMATIC) == true) {
                        // Depth occlusion toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = depthSettings.useDepthForOcclusion(),
                                onCheckedChange = {
                                    depthSettings.setUseDepthForOcclusion(it)
                                }
                            )
                            Text(
                                text = "Use depth for occlusion",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Depth visualization toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = depthSettings.depthColorVisualizationEnabled(),
                                onCheckedChange = {
                                    depthSettings.setDepthColorVisualizationEnabled(it)
                                }
                            )
                            Text(
                                text = "Show depth visualization",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    } else {
                        Text(stringResource(R.string.options_title_without_depth))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }
}