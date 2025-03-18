package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.getPngImageForModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
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
private var rotationJob: Job? = null  // Coroutine for smooth rotation
private var isRotationActive = false  // Tracks rotation state
private var isWaitingForToggle = false // Prevents rapid toggling

@Composable
fun ARDepthEstimationScreen(
    initialModelFileName: String,
    viewModel: ARDepthEstimationViewModel = hiltViewModel()
) {
    var selectedModel by remember { mutableStateOf(initialModelFileName) }
    var activeModelNode by remember { mutableStateOf<ArModelNode?>(null) } // Tracks selected model node
    var showModelSelectionSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        val arSceneView = remember { mutableStateOf<ArSceneView?>(null) }

        ARScene(
            modifier = Modifier.fillMaxSize(),
            planeRenderer = true,
            onCreate = { sceneView ->
                arSceneView.value = sceneView
                Log.d(TAG, "✅ ARScene created successfully")
            },


        onTap = { hitResult ->
            val pose = hitResult.hitPose
            val anchor = hitResult.createAnchor()

            arSceneView.value?.let { sceneView ->
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
                        },
                        onError = { error ->
                            Log.e(TAG, "❌ Error loading model: ${error.message}")
                        }
                    )



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

                            rotationJob?.cancel()  // Stop any active rotation job
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

                modelNode.isEditable = true
                modelNode.isSelectable = true
                modelNode.isScaleEditable = true
                modelNode.isRotationEditable = false  // Rotation enabled only via double tap
                activeModelNode = modelNode
                sceneView.addChild(modelNode)
                viewModel.addModelToList(modelNode)
                Log.d(TAG, "✅ Model added to AR scene")
            }
        }

        )

        Button(
            onClick = { showModelSelectionSheet = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Add Model")
        }

        Button(
            onClick = {
                viewModel.saveCurrentLayout()
                Log.d(TAG, "💾 Layout saved with ${viewModel.savedModels.value.size} models")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Save Layout")
        }

        if (showModelSelectionSheet) {
            ModelSelectionBottomSheet(
                onModelSelected = { selectedModelName ->
                    selectedModel = selectedModelName
                    viewModel.selectedModel(selectedModelName)
                    Log.d(TAG, "✅ Model selected from BottomSheet: $selectedModelName")
                    showModelSelectionSheet = false
                },
                onDismiss = { showModelSelectionSheet = false }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionBottomSheet(
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val models = listOf(
        ModelData(getPngImageForModel("android robot.glb"), "android robot.glb"),
        ModelData(getPngImageForModel("Black Chair.glb"), "Black Chair.glb"),
        ModelData(getPngImageForModel("White Chair.glb"), "White Chair.glb"),
        ModelData(getPngImageForModel("Gray Chair.glb"), "Gray Chair.glb"),
        ModelData(getPngImageForModel("Brown Table 1.glb"), "Brown Table 1.glb"),
        ModelData(getPngImageForModel("Brown Table 2.glb"), "Brown Table 2.glb"),
        ModelData(getPngImageForModel("White Table.glb"), "White Table.glb"),
        ModelData(getPngImageForModel("Red Couch.glb"), "Red Couch.glb"),
        ModelData(getPngImageForModel("Brown Couch.glb"), "Brown Couch.glb"),
        ModelData(getPngImageForModel("White Couch.glb"), "White Couch.glb")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select a 3D Model", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))

            models.forEach { model ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onModelSelected(model.modelFileName) }
                ) {
                    Icon(
                        painter = painterResource(id = model.imageResId),
                        contentDescription = model.modelFileName,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(model.modelFileName)
                }
            }
        }
    }
}

@Composable
fun RotationControlBar(
    modifier: Modifier = Modifier,
    rotationValue: Float,
    onRotationChange: (Float) -> Unit
) {
    Slider(
        value = rotationValue,
        onValueChange = { newRotation ->
            // Normalize rotation to stay between 0° - 360°
            val normalizedRotation = ((newRotation % 360) + 360) % 360
            onRotationChange(normalizedRotation)
        },
        valueRange = 0f..360f,
        modifier = modifier
            .fillMaxWidth(0.75f)
            .padding(horizontal = 16.dp),
        colors = SliderDefaults.colors(
            thumbColor = Color.Blue,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White
        )
    )
}















// 2
//package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.media.Image
//import android.util.Log
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import com.google.ar.core.*
//import dev.romainguy.kotlin.math.*
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.arcore.position
//import io.github.sceneview.ar.arcore.rotation
//import io.github.sceneview.ar.node.ArModelNode
//import io.github.sceneview.gesture.GestureDetector
//import io.github.sceneview.math.Position
//import io.github.sceneview.math.Rotation
//import java.nio.ByteBuffer
//
//private const val TAG = "ARDepthScreen"
//
//@Composable
//fun ARDepthEstimationScreen() {
//    var modelNode: ArModelNode? by remember { mutableStateOf(null) }
//    var lastLogTime by remember { mutableStateOf(System.currentTimeMillis()) }
//    var latestDepthImage by remember { mutableStateOf<Image?>(null) }
//
//    ARScene(
//        modifier = Modifier.fillMaxSize(),
//        planeRenderer = true,
//
//        onSessionCreate = { session ->
//            Log.d(TAG, "🔄 Configuring AR Session...")
//            session.configure(Config(session).apply {
//                depthMode = if (session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY)) {
//                    Log.d(TAG, "✅ Depth Mode: RAW_DEPTH_ONLY enabled")
//                    Config.DepthMode.RAW_DEPTH_ONLY
//                } else {
//                    Log.w(TAG, "⚠️ Depth Mode: RAW_DEPTH_ONLY not supported, disabling depth")
//                    Config.DepthMode.DISABLED
//                }
//                instantPlacementMode = Config.InstantPlacementMode.DISABLED
//                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
//                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//            })
//            Log.d(TAG, "✅ AR Session configured successfully!")
//        },
//
//        onTap = { hitResult ->
//            Log.d(TAG, "🚀 Tap detected! Processing hitResult...")
//
//            if (modelNode != null) {
//                Log.d(TAG, "❗ Only one model allowed.")
//                return@ARScene
//            }
//
//            val pose = hitResult.hitPose
//            val anchor = hitResult.createAnchor()
//
//            latestDepthImage?.let { depthImage ->
//                try {
//                    val normal = computeSurfaceNormal(depthImage, pose)
//                    Log.d(TAG, "🔄 Surface Normal: $normal")
//
//                    modelNode = ArModelNode(this.engine).apply {
//                        this.anchor = anchor
//                        this.position = Position(pose.position)
//                        this.rotation = Rotation(hitResult.hitPose.rotation)
//
//                        loadModelGlbAsync(
//                            "android robot.glb",
//                            autoAnimate = true,
//                            onLoaded = { Log.d(TAG, "✅ Model loaded successfully!") },
//                            onError = { Log.e(TAG, "❌ Model load failed: ${it.localizedMessage}") }
//                        )
//
//                        // 🔄 Smooth Rotation with Double Tap
//                        onDoubleTapEvent = listOf { event ->
//                            val deltaX = (event.motionEvent.x - event.motionEvent.downTime) * 0.05f
//                            val deltaY = (event.motionEvent.y - event.motionEvent.downTime) * 0.05f
//
//                            this.rotation = Rotation(
//                                y = this.rotation.y + deltaX,
//                                x = this.rotation.x + deltaY
//                            )
//
//                            Log.d(TAG, "🔄 Smooth Rotation: X=${this.rotation.x}, Y=${this.rotation.y}")
//                        }
//                    }
//
//                    addChild(modelNode!!)
//                    Log.d(TAG, "🎯 Model anchored and placed successfully!")
//                } catch (e: Exception) {
//                    Log.e(TAG, "⚠️ Depth Processing Error: ${e.localizedMessage}")
//                }
//            } ?: Log.e(TAG, "⚠️ No depth image available yet!")
//        },
//
//        onFrame = { arFrame ->
//            val currentTime = System.currentTimeMillis()
//            if (currentTime - lastLogTime >= 3000) {
//                try {
//                    latestDepthImage?.close() // Close previous image
//                    latestDepthImage = arFrame.frame.acquireRawDepthImage16Bits()
//
//                    latestDepthImage?.let {
//                        Log.d(TAG, "🌟 Depth Image Captured | Size: ${it.width}x${it.height}")
//                    }
//
//                    lastLogTime = currentTime
//                } catch (e: Exception) {
//                    Log.e(TAG, "⚠️ Depth Processing Error: ${e.localizedMessage}")
//                }
//            }
//        }
//    )
//}
//
///**
// * Compute surface normal from depth data at given pose
// */
//fun computeSurfaceNormal(depthData: Image, pose: Pose): Float3 {
//    val width = depthData.width
//    val height = depthData.height
//    val centerX = width / 2
//    val centerY = height / 2
//
//    val depthCenter = getDepthAt(depthData, centerX, centerY)
//    val depthRight = getDepthAt(depthData, centerX + 1, centerY)
//    val depthDown = getDepthAt(depthData, centerX, centerY + 1)
//
//    val p0 = Float3(0f, 0f, depthCenter)
//    val p1 = Float3(1f, 0f, depthRight)
//    val p2 = Float3(0f, 1f, depthDown)
//
//    return crossProduct(p1 - p0, p2 - p0)
//}
//
///**
// * Extract depth value from Image at given (x, y) coordinate
// */
//fun getDepthAt(image: Image, x: Int, y: Int): Float {
//    val buffer: ByteBuffer = image.planes[0].buffer
//    val rowStride = image.planes[0].rowStride
//    val index = y * rowStride + x * 2 // Each pixel is 2 bytes
//
//    return buffer.getShort(index).toFloat() / 1000f // Convert mm to meters
//}
//
///**
// * Compute the cross product of two Float3 vectors
// */
//fun crossProduct(a: Float3, b: Float3): Float3 {
//    return Float3(
//        a.y * b.z - a.z * b.y,
//        a.z * b.x - a.x * b.z,
//        a.x * b.y - a.y * b.x
//    )
//}





// 1
//package com.jsb.arhomerenovat.feature_ar_home.presentation.ui
//
//import android.util.Log
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import com.google.ar.core.*
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.arcore.rotation
//import io.github.sceneview.ar.node.ArModelNode
//import io.github.sceneview.math.Rotation
//
//// Log tag for debugging
//private const val TAG = "ARStableScreen"
//
//@Composable
//fun ARDepthEstimationScreen() {
//    // Mutable state to keep track of already anchored planes
//    var anchoredPlanes by remember { mutableStateOf(mutableSetOf<Anchor>()) }
//
//    ARScene(
//        modifier = Modifier.fillMaxSize(),
//        planeRenderer = true, // Enables the visualization of detected planes
//
//        // ✅ Configure AR session when it is created
//        onSessionCreate = { session ->
//            session.configure(Config(session).apply {
//                // ✅ Enable depth mode only if the device supports it
//                depthMode = if (session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY))
//                    Config.DepthMode.RAW_DEPTH_ONLY else Config.DepthMode.DISABLED
//
//                // ✅ Disable Instant Placement (we want only accurate tracking)
//                instantPlacementMode = Config.InstantPlacementMode.DISABLED
//
//                // ✅ Enable environmental HDR for better lighting effects
//                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
//
//                // ✅ Detect both horizontal and vertical planes
//                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//            })
//
//            Log.d(TAG, "✅ AR Session configured successfully.")
//        },
//
//        // ✅ Handles tap events to place 3D models on detected planes
//        onTap = { hitResult ->
//            Log.d(TAG, "🚀 Plane tapped at: ${hitResult.hitPose.translation.joinToString()}")
//
//            // Extract the detected trackable (plane) from the hit result
//            val trackable = hitResult.trackable
//            if (trackable is Plane && trackable.trackingState == TrackingState.TRACKING) {
//
//                // ✅ Ensure we are not adding duplicate anchors
//                if (!anchoredPlanes.contains(trackable.createAnchor(hitResult.hitPose))) {
//                    val anchor = trackable.createAnchor(hitResult.hitPose) // Create anchor at detected position
//                    anchoredPlanes.add(anchor) // Store anchored plane to prevent flickering
//
//                    // ✅ Create a 3D model and attach it to the anchor
//                    val modelNode = ArModelNode(this.engine).apply {
//                        this.anchor = anchor // Keep the model anchored at this position
//                        rotation = Rotation(hitResult.hitPose.rotation) // Maintain rotation
//
//                        // ✅ Load the 3D model (Android robot) asynchronously
//                        loadModelGlbAsync("android robot.glb", autoAnimate = true,
//                            onError = { Log.e(TAG, "❌ Model load failed: ${it.localizedMessage}") },
//                            onLoaded = { Log.d(TAG, "✅ Model loaded successfully.") }
//                        )
//                    }
//
//                    // ✅ Add the 3D model to the AR scene
//                    addChild(modelNode)
//                    Log.d(TAG, "📌 Plane anchored and model placed successfully.")
//                }
//            }
//        },
//
//        // ✅ Frame updates: Enable plane visualization only when tracking
//        onFrame = { arFrame ->
//            planeRenderer.isEnabled = arFrame.camera.trackingState == TrackingState.TRACKING
//        }
//    )
//}
