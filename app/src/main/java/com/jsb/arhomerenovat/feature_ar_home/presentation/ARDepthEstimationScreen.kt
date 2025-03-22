package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Earth
import com.google.ar.core.TrackingState
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.ModelSelectionBottomSheet
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.getPngImageForModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArSession
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.localPosition
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


private const val TAG = "ARDepthScreen"

@Composable
fun ARDepthEstimationScreen(
    initialModelFileName: String,
    viewModel: ARDepthEstimationViewModel = hiltViewModel()
) {
    var selectedModel by remember { mutableStateOf(initialModelFileName) }
    var activeModelNode by remember { mutableStateOf<ArModelNode?>(null) }
    var showModelSelectionSheet by remember { mutableStateOf(false) }
    val arSceneView = remember { mutableStateOf<ArSceneView?>(null) }

    var isWaitingForToggle by remember { mutableStateOf(false) }
    var isRotationActive by remember { mutableStateOf(false) }
    var rotationJob: Job? by remember { mutableStateOf(null) }

    var savedModels by remember { mutableStateOf<List<ModelEntity>>(emptyList()) }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            planeRenderer = true,
            onCreate = { sceneView ->
                arSceneView.value = sceneView
                Log.d(TAG, "✅ AR Scene Created")
            },

            onSessionCreate = { session ->
                if (!session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED)) {
                    Log.e(TAG, "❌ Geospatial mode is not supported on this device.")
                    return@ARScene
                }
                session.configure(Config(session).apply {
                    geospatialMode = Config.GeospatialMode.ENABLED
                    depthMode = if (session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY)) {
                        Config.DepthMode.RAW_DEPTH_ONLY
                    } else {
                        Config.DepthMode.DISABLED
                    }
                    planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                })
                Log.d(TAG, "✅ AR Session configured successfully with Geospatial API.")
            },

            onTap = { hitResult ->
                val pose = hitResult.hitPose
                val anchor = hitResult.createAnchor()
                arSceneView.value?.let { sceneView ->
                    // ✅ ModelNode with Enhanced Features
                    val modelNode = ArModelNode(sceneView.engine).apply {
                        loadModelGlbAsync(
                            glbFileLocation = selectedModel,
                            scaleToUnits = 0.2f,
                            onLoaded = {
                                Log.d(TAG, "✅ Model loaded successfully: $selectedModel")

                                this.anchor = anchor
                                this.isVisible = true
                                this.rotation = Rotation(pose.rotation)
                                this.position = Position(pose.position)
                                // 🚀 Storing the active model
                                activeModelNode = this
                            },
                            onError = { error ->
                                Log.e(TAG, "❌ Error loading model: ${error.message}")
                            }
                        )

                        // 🔄 Double-Tap Rotation Logic Implemented Inside `val modelNode`
                        onDoubleTapEvent = listOf {
                            if (isWaitingForToggle) return@listOf // Ignore rapid successive taps

                            isWaitingForToggle = true
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(300) // Buffer to prevent double activation
                                isWaitingForToggle = false
                            }

                            if (!isRotationActive) {
                                Log.d(TAG, "▶️ Rotation Enabled")

                                isRotationActive = true
                                this.isScaleEditable = false
                                this.isEditable = false
                                this.isRotationEditable = true

                                rotationJob?.cancel() // Stop any active rotation job
                                rotationJob = CoroutineScope(Dispatchers.Default).launch {
                                    while (isActive && isRotationActive) {
                                        arSceneView.value?.hitTest(
                                            position = hitResult.hitPose.position,
                                            plane = true,
                                            depth = true,
                                            instant = true
                                        )?.let { hitResult ->
                                            val newPose = hitResult.hitPose
                                            this@apply.rotation = Rotation(y = newPose.rotation.y)
                                            Log.d(TAG, "🔄 Rotating... ${this@apply.rotation.y} degrees")
                                        }
                                        delay(50) // Controls rotation speed
                                    }
                                }
                            } else {
                                Log.d(TAG, "⏹️ Rotation Disabled")

                                isRotationActive = false
                                this.isScaleEditable = true
                                this.isEditable = true
                                this.isRotationEditable = false

                                rotationJob?.cancel()  // Stop the rotation job
                            }
                        }
                    }

                    sceneView.addChild(modelNode)
                    Log.d(TAG, "✅ Model added to AR scene")
                }
            }
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)  // Adds spacing between rows
            ) {
                // First row with "Save Model" and "Add Model" buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            activeModelNode?.let { modelNode ->
                                val session = arSceneView.value?.arSession
                                val earth = session?.earth

                                if (earth != null) {
                                    Log.d(TAG, "🌍 Earth Tracking State: ${earth.trackingState}")
                                    Log.d(TAG, "🌍 Earth State: ${earth.earthState}")

                                    if (earth.trackingState == TrackingState.TRACKING) {
                                        val geospatialPose = earth.cameraGeospatialPose
                                        Log.d(TAG, "🌍 Geospatial Data Retrieved:")
                                        Log.d(TAG, "📍 Latitude: ${geospatialPose.latitude}")
                                        Log.d(TAG, "📍 Longitude: ${geospatialPose.longitude}")
                                        Log.d(TAG, "📍 Altitude: ${geospatialPose.altitude}")

                                        viewModel.saveGeoModelToDatabase(
                                            modelName = selectedModel,
                                            posX = modelNode.position.x,
                                            posY = modelNode.position.y,
                                            posZ = modelNode.position.z,
                                            qx = modelNode.quaternion.x,
                                            qy = modelNode.quaternion.y,
                                            qz = modelNode.quaternion.z,
                                            qw = modelNode.quaternion.w,
                                            latitude = geospatialPose.latitude,
                                            longitude = geospatialPose.longitude,
                                            altitude = geospatialPose.altitude
                                        )
                                        Log.d(TAG, "💾 Model successfully saved with geospatial data: $selectedModel")
                                    } else {
                                        Log.e(TAG, "❌ Geospatial data unavailable or tracking state not stable yet.")
                                    }
                                } else {
                                    Log.e(TAG, "❌ Earth object is null.")
                                }
                            } ?: Log.e(TAG, "❌ No active model found to save.")
                        }
                    ) {
                        Text("Save Model")
                    }

                    Button(onClick = { showModelSelectionSheet = true }) {
                        Text("Add Model")
                    }
                }

                // Second row with "Clear Models" and "Load Saved Models" buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.clearGeoModels()
                            arSceneView.value?.let { sceneView ->
                                sceneView.children.forEach { sceneView.removeChild(it) }
                            }
                            activeModelNode = null
                            Log.d(TAG, "🗑️ All models cleared")
                        }
                    ) {
                        Text("Clear Models")
                    }

                    Button(
                        onClick = {
                            viewModel.fetchSavedGeoModels { models ->
                                savedModels = models  // Update state with fetched models
                                arSceneView.value?.let { sceneView ->
                                    loadSavedModels(sceneView, models)
                                }
                            }
                        }
                    ) {
                        Text("Load Saved Models")
                    }
                }
            }
        }
    }

    if (showModelSelectionSheet) {
        ModelSelectionBottomSheet(
            onModelSelected = { selectedModelName ->
                selectedModel = selectedModelName
                viewModel.selectedGeoModel(selectedModelName)
                Log.d(TAG, "✅ Model selected from BottomSheet: $selectedModelName")
                showModelSelectionSheet = false
            },
            onDismiss = { showModelSelectionSheet = false }
        )
    }
}

private fun loadSavedModels(sceneView: ArSceneView, models: List<ModelEntity>) {
    val session = sceneView.arSession ?: return
    val earth = session.earth ?: return

    if (earth.earthState != Earth.EarthState.ENABLED) {
        Log.e(TAG, "❌ Geospatial API not supported or not enabled.")
        return
    }

    if (earth.trackingState != TrackingState.TRACKING) {
        Log.e(TAG, "❌ Earth tracking state is not stable yet.")
        return
    }

    models.forEach { savedModel ->
        val earthAnchor = earth.createAnchor(
            savedModel.latitude, savedModel.longitude, savedModel.altitude,
            savedModel.qx, savedModel.qy, savedModel.qz, savedModel.qw
        )

        val modelNode = ArModelNode(sceneView.engine).apply {
            loadModelGlbAsync(
                glbFileLocation = savedModel.modelName,
                scaleToUnits = 0.2f,
                onLoaded = {
                    Log.d(TAG, "✅ Loaded model: ${savedModel.modelName}")
                    this.anchor = earthAnchor
                    this.isVisible = true
                },
                onError = { error ->
                    Log.e(TAG, "❌ Error loading model: ${error.message}")
                }
            )
        }

        sceneView.addChild(modelNode)
    }
    Log.d(TAG, "✅ All saved models loaded into AR scene with Earth Anchors")
}
