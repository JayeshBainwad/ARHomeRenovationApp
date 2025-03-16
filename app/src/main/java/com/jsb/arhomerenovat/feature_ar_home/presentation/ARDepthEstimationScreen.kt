package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale

private const val TAG = "ARDepthScreen"

@Composable
fun ARDepthEstimationScreen(modelFileName: String) {
    var modelNode: ArModelNode? by remember { mutableStateOf(null) }
    var isModelSelected by remember { mutableStateOf(false) }
    var rotationValue by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ARScene(
            modifier = Modifier
                .fillMaxSize(),
            planeRenderer = true,
            onTap = { hitResult ->
                if (modelNode == null) {
                    val pose = hitResult.hitPose
                    val anchor = hitResult.createAnchor()

                    modelNode = ArModelNode(this.engine).apply {
                        this.anchor = anchor
                        this.position = Position(pose.position)
                        this.rotation = Rotation(pose.rotation)

                        // 🔹 Correct Scaling Logic
                        this.scale = when (modelFileName) {
                            "android robot.glb" -> Scale(1.0f)   // Normal scale
                            "Black Chair.glb", "White Chair.glb" -> Scale(0.3f)  // Reduce oversized models
                            "Brown Table 1.glb", "Brown Table 2.glb" -> Scale(0.3f)
                            else -> Scale(0.2f)  // Default scale for unknown models
                        }

                        loadModelGlbAsync(
                            modelFileName, // 🔹 Dynamic model loading
                            autoAnimate = true,
                            onLoaded = { Log.d(TAG, "✅ Model loaded successfully!") },
                            onError = { Log.e(TAG, "❌ Model load failed: ${it.localizedMessage}") }
                        )
                    }

                    addChild(modelNode!!)
                    modelNode?.isPositionEditable = true
                    modelNode?.isRotationEditable = true
                    modelNode?.isScaleEditable = true
                    isModelSelected = true
                    Log.d(TAG, "🎯 Model anchored and placed successfully!")
                } else {
                    isModelSelected = !isModelSelected
                    Log.d(TAG, if (isModelSelected) "✔️ Model selected." else "❌ Model deselected.")
                }
            }
        )

        // 🔄 Rotation Control Bar - Overlay on Camera Feed
        RotationControlBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            rotationValue = rotationValue,
            onRotationChange = { newRotation ->
                rotationValue = newRotation
                modelNode?.rotation = Rotation(y = rotationValue)
                Log.d(TAG, "🔄 Rotation Bar Value: $rotationValue")
            }
        )
    }
}

@Composable
fun RotationControlBar(
    modifier: Modifier = Modifier,
    rotationValue: Float,
    onRotationChange: (Float) -> Unit
) {
    var initialOffset by remember { mutableStateOf(0f) }  // 🔒 Prevent sudden jump

    Slider(
        value = rotationValue,
        onValueChange = { newRotation ->
            if (initialOffset == 0f) {
                // ✅ Set the initialOffset only when touched for the first time
                initialOffset = newRotation
            }

            val adjustedRotation = newRotation - initialOffset
            onRotationChange(adjustedRotation)  // 🔄 Smooth rotation logic
        },
        onValueChangeFinished = {
            // ✅ Reset initialOffset when user releases touch
            initialOffset = 0f
        },
        valueRange = -450f..450f,
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
