//import android.graphics.Bitmap
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.google.ar.core.Config
//import com.google.ar.core.TrackingState
//import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
//import com.jsb.arhomerenovat.feature_ar_home.presentation.util.ModelSelectionBottomSheet
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.ArSceneView
//import io.github.sceneview.ar.arcore.position
//import io.github.sceneview.ar.arcore.quaternion
//import io.github.sceneview.ar.arcore.rotation
//import io.github.sceneview.ar.node.ArModelNode
//import io.github.sceneview.math.Position
//import io.github.sceneview.math.Rotation
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch

//package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.util.Log
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.google.ar.core.Config
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.ArSceneView
//import io.github.sceneview.ar.arcore.planeFindingEnabled
//import io.github.sceneview.ar.node.ArModelNode
//import io.github.sceneview.math.Position
//import kotlin.math.sqrt
//
//private const val TAG = "ARDepthScreen"
//
//@Composable
//fun ARDepthEstimationScreen(
//    initialModelFileName: String,
//    viewModel: ARDepthEstimationViewModel = hiltViewModel()
//) {
//    val arSceneView = remember { mutableStateOf<ArSceneView?>(null) }
//    val modelNodes = remember { mutableStateListOf<ArModelNode>() }
//    var distance by remember { mutableStateOf(0f) }
//    var pointLabel by remember { mutableStateOf("Tap to select Point 1") }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Box(modifier = Modifier.weight(1f)) {
//            ARScene(
//                modifier = Modifier.fillMaxSize(),
//                planeRenderer = true,
//                onCreate = { sceneView ->
//                    arSceneView.value = sceneView
//                    Log.d(TAG, "✅ AR Scene Created")
//                },
//                onSessionCreate = { session ->
//                    try {
//                        Log.d(TAG, "✅ AR Session Created")
//                        val config = session.config.apply {
//                            geospatialMode = Config.GeospatialMode.ENABLED
//                            depthMode = Config.DepthMode.AUTOMATIC
//                            planeFindingEnabled = true
//                            planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//                        }
//                        session.configure(config)
//                    } catch (e: Exception) {
//                        Log.e(TAG, "❌ Error configuring AR Session: ${e.localizedMessage}")
//                    }
//                },
//                onTap = { hitResult ->
//                    val pose = hitResult.hitPose
//                    val anchor = hitResult.createAnchor()
//
//                    arSceneView.value?.let { sceneView ->
//
//                        // Ensure only two models exist at a time
//                        if (modelNodes.size >= 2) {
//                            sceneView.removeChild(modelNodes.first()) // Remove the first one
//                            modelNodes.removeFirst()
//                        }
//
//                        // Create and place the model
//                        val modelNode = ArModelNode(sceneView.engine).apply {
//                            loadModelGlbAsync(
//                                glbFileLocation = "android robot.glb",
//                                scaleToUnits = 0.3f,
//                                onLoaded = {
//                                    Log.d(TAG, "✅ Model loaded successfully")
//
//                                    this.anchor = anchor
//                                    this.isVisible = true
//                                    this.isScaleEditable = false
//                                    this.isEditable = false
//                                    this.isRotationEditable = false
//                                    this.position = Position(pose.tx(), pose.ty(), pose.tz()) // Store world position
//                                },
//                                onError = { error ->
//                                    Log.e(TAG, "❌ Error loading model: ${error.message}")
//                                }
//                            )
//                        }
//                        sceneView.addChild(modelNode)
//                        modelNodes.add(modelNode)
//
//                        // Update label & calculate distance if two models exist
//                        if (modelNodes.size == 1) {
//                            pointLabel = "Tap to select Point 2"
//                        } else if (modelNodes.size == 2) {
//                            pointLabel = "Tap to select Point 1"
//                            distance = calculateDistance(modelNodes[0].position, modelNodes[1].position)
//                        }
//                    }
//                }
//            )
//        }
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = pointLabel, style = MaterialTheme.typography.bodyLarge)
//
//            if (distance > 0) {
//                Text("Distance: ${"%.2f".format(distance)} meters")
//            }
//        }
//    }
//}
//
//// Function to calculate the real-world distance between two 3D models
//fun calculateDistance(pos1: Position, pos2: Position): Float {
//    val dx = pos1.x - pos2.x
//    val dy = pos1.y - pos2.y
//    val dz = pos1.z - pos2.z
//    return sqrt(dx * dx + dy * dy + dz * dz)
//}





// Working. MiDaS Depth map display with ARScene().
package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.presentation.util.ModelSelectionBottomSheet
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.ar.core.ArCoreApk
import io.github.sceneview.node.Node

private const val TAG = "ARDepthScreen"

@Composable
fun ARDepthEstimationScreen(
    initialModelFileName: String,
    navigate: NavController,
    viewModel: ARDepthEstimationViewModel = hiltViewModel(),
) {

    var selectedModel by remember { mutableStateOf(initialModelFileName) }
    var activeModelNode by remember { mutableStateOf<ArModelNode?>(null) }
    var showModelSelectionSheet by remember { mutableStateOf(false) }
    val arSceneView = remember { mutableStateOf<ArSceneView?>(null) }

    var isWaitingForToggle by remember { mutableStateOf(false) }
    var isRotationActive by remember { mutableStateOf(false) }
    var rotationJob: Job? by remember { mutableStateOf(null) }

    val addedModels = remember { mutableStateListOf<ModelEntity>() }
    var isModelPlaced by remember { mutableStateOf(false) } // Restrict multiple model placements
    var isSaveButtonEnabled by remember { mutableStateOf(false) } // Restrict multiple model placements

    val depthBitmap by viewModel.depthBitmap.collectAsState()
    var isDepthCaptureActive by remember { mutableStateOf(false) }

    // Use the exact aspect ratio from your depth map (example: 4:3)
    val depthMapAspectRatio = 2f / 3f  // Change this to match your actual depth map ratio

    // Calculate height based on screen width and depth map ratio
    val arViewHeight = (LocalConfiguration.current.screenWidthDp.dp / depthMapAspectRatio)
    val buttonAreaHeight = LocalConfiguration.current.screenHeightDp.dp - arViewHeight
    var lastDepthBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    var isPlaneDetected by remember { mutableStateOf(false) }

    var lastTapTime by remember { mutableStateOf(0L) }
    val doubleTapThreshold = 300L // milliseconds between taps to count as double tap
    var isDoubleTapHandled by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(isDepthCaptureActive) {
        if (isDepthCaptureActive) {
            // Continuously capture depth frames when active
            while (isDepthCaptureActive) {
                viewModel.captureDepthFrame { success ->
                    if (success) {
                        lastDepthBitmap = depthBitmap
                    }
                }
                delay(1000) // Adjust this delay as needed for performance
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera and Depth Map Container
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .clipToBounds()
        ) {
            // AR Camera View (matched to depth map specs)
            ARScene(
                modifier = Modifier
                    .aspectRatio(depthMapAspectRatio)
                    .fillMaxSize(),
                onCreate = { sceneView ->
                    arSceneView.value = sceneView
                    viewModel.setArSceneView(sceneView)
                    Log.d(TAG, "✅ AR Scene Created")

                    // Check ARCore availability
                    val availability = ArCoreApk.getInstance().checkAvailability(context)
                    if (availability.isTransient || !availability.isSupported) {
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar(
                                message = when {
                                    availability.isTransient -> "AR features temporarily unavailable"
                                    !availability.isSupported -> "AR not supported on this device"
                                    else -> "AR features unavailable"
                                },
                                duration = SnackbarDuration.Long
                            )
                        }
                        return@ARScene
                    }

                    // Disable plane visualization
                    sceneView.planeRenderer.isVisible = false
                    sceneView.planeRenderer.isEnabled = true
                },
//                onCreate = { sceneView ->
//                    arSceneView.value = sceneView
//                    viewModel.setArSceneView(sceneView)
//                    Log.d(TAG, "✅ AR Scene Created")
//
//                    // Disable plane visualization
//                    sceneView.planeRenderer.isVisible = false
//                    sceneView.planeRenderer.isEnabled = true
//                },
                onSessionCreate = { session ->
                    Log.d(TAG, "✅ AR Session Created")

                    // Check device capabilities
                    val config = session.config.apply {
                        // Check geospatial support
                        geospatialMode = when {
                            session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED) -> {
                                Log.d(TAG, "✅ Geospatial Mode Supported")
                                Config.GeospatialMode.ENABLED
                            }
                            else -> {
                                Log.w(TAG, "⚠️ Geospatial Mode Not Supported")
                                Config.GeospatialMode.DISABLED
                            }
                        }

                        // Check depth support
                        depthMode = when {
                            session.isDepthModeSupported(Config.DepthMode.AUTOMATIC) -> {
                                Log.d(TAG, "✅ Depth Mode Supported")
                                Config.DepthMode.AUTOMATIC
                            }
                            session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY) -> {
                                Log.w(TAG, "⚠️ Only Raw Depth Supported")
                                Config.DepthMode.RAW_DEPTH_ONLY
                            }
                            else -> {
                                Log.w(TAG, "⚠️ Depth Not Supported")
                                Config.DepthMode.DISABLED
                            }
                        }

                        // Check plane detection support
                        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    }

                    session.configure(config)

                    // Show capability warnings to user
                    CoroutineScope(Dispatchers.Main).launch {
                        if (config.geospatialMode == Config.GeospatialMode.DISABLED) {
                            snackbarHostState.showSnackbar(
                                message = "Geospatial features not available",
                                duration = SnackbarDuration.Long
                            )
                        }
                        if (config.depthMode == Config.DepthMode.DISABLED) {
                            snackbarHostState.showSnackbar(
                                message = "Depth features not available",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                },
//                onSessionCreate = { session ->
//                    Log.d(TAG, "✅ AR Session Created")
//
//                    // ✅ Enable Geospatial & Depth Mode
//                    val config = session.config.apply {
//                        geospatialMode = Config.GeospatialMode.ENABLED
//                        depthMode = Config.DepthMode.AUTOMATIC // ✅ Enable Depth Mode
//                        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//                    }
//                    session.configure(config)
//                },
                onFrame = {
                    // Check for plane detection
                    val wasPlaneDetected = isPlaneDetected
                    isPlaneDetected = arSceneView.value?.arSession?.let { session ->
                        session.getAllTrackables(com.google.ar.core.Plane::class.java)
                            .any { it.trackingState == TrackingState.TRACKING }
                    } ?: false

                    if (isPlaneDetected && !wasPlaneDetected) {
                        // Show snackbar when plane is first detected
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar(
                                message = "Start placing the 3D object",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                onTap = { hitResult ->

                    if (isModelPlaced) {
                        Log.d(TAG, "⚠️ A model is already placed. Save it before adding another.")
                        return@ARScene
                    }

                    val pose = hitResult.hitPose
                    val anchor = hitResult.createAnchor()
                    arSceneView.value?.let { sceneView ->
                        val earth = sceneView.arSession?.earth
                        if (earth == null || earth.trackingState != TrackingState.TRACKING) {
                            Log.e(TAG, "❌ Earth is not tracking. Cannot place model.")
                            return@let
                        }

                        val geospatialPose = earth.cameraGeospatialPose
                        val latitude = geospatialPose.latitude
                        val longitude = geospatialPose.longitude
                        val altitude = geospatialPose.altitude

                        val modelNode = ArModelNode(sceneView.engine).apply {
                            loadModelGlbAsync(
                                glbFileLocation = selectedModel,
                                scaleToUnits = 0.3f,
                                onLoaded = {
                                    Log.d(TAG, "✅ Model loaded successfully: $selectedModel")

                                    this.anchor = anchor
                                    this.isVisible = true
                                    this.isEditable = true
                                    this.isRotationEditable = false
                                    this.isScaleEditable = true
                                    this.rotation = Rotation(pose.rotation)
                                    this.position = Position(pose.position)
                                    this.isSelectable = true

                                    activeModelNode = this
                                    isModelPlaced = true // Restrict new model addition
                                    isSaveButtonEnabled = true

                                    // ✅ Store model data with geospatial values
                                    addedModels.add(
                                        ModelEntity(
                                            layoutId = 0, // Will be updated when saving
                                            modelName = selectedModel,
                                            posX = pose.position.x,
                                            posY = pose.position.y,
                                            posZ = pose.position.z,
                                            qx = pose.quaternion.x,
                                            qy = pose.quaternion.y,
                                            qz = pose.quaternion.z,
                                            qw = pose.quaternion.w,
                                            scaleX = 1.0f,
                                            scaleY = 1.0f,
                                            scaleZ = 1.0f,
                                            latitude = latitude,
                                            longitude = longitude,
                                            altitude = altitude
                                        )
                                    )

                                    Log.d(TAG, "🌍 Geospatial Data -> Lat: $latitude, Long: $longitude, Alt: $altitude")
                                },
                                onError = { error ->
                                    Log.e(TAG, "❌ Error loading model: ${error.message}")
                                }
                            )

                            onSingleTapUp = listOf {
                                // Check if this is part of a double tap
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTapTime < doubleTapThreshold) {
                                    isDoubleTapHandled = true
                                    return@listOf
                                }
                                lastTapTime = currentTime

                                // Use a coroutine to delay the single tap action slightly
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(doubleTapThreshold / 2) // Wait half the double tap threshold

                                    // If a double tap was detected during this time, don't execute single tap
                                    if (!isDoubleTapHandled) {
                                        // Deselect previous selection if any
                                        activeModelNode?.selectionVisualizer = null

                                        // Select this model
                                        isSaveButtonEnabled = true
                                        activeModelNode = this@apply
                                        this@apply.isEditable = true
                                        this@apply.isScaleEditable = true

                                        // Create selection visualization
                                        this@apply.selectionVisualizer = Node(this@apply.engine).apply {
                                            scale = Position(1.1f, 1.1f, 1.1f)
                                        }

                                        snackbarHostState.showSnackbar(
                                            message = "Model selected",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    isDoubleTapHandled = false // Reset for next gesture
                                }
                            }

                            // 🔄 Disable Editing After Saving
                            onDoubleTapEvent = listOf {
                                isDoubleTapHandled = true // Mark that we're handling a double tap

                                if (isWaitingForToggle) return@listOf

                                isWaitingForToggle = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(300)
                                    isWaitingForToggle = false
                                }

                                if (!isRotationActive) {
                                    Log.d(TAG, "▶️ Rotation Enabled")
                                    isRotationActive = true
                                    this.isScaleEditable = false
                                    this.isEditable = false
                                    this.isRotationEditable = true

                                    rotationJob?.cancel()
                                    rotationJob = CoroutineScope(Dispatchers.Default).launch {
                                        while (isActive && isRotationActive) {
                                            arSceneView.value?.hitTest(
                                                position = hitResult.hitPose.position,
                                                plane = true,
                                                depth = true,
                                                instant = true
                                            )?.let { hitResult ->
                                                val newPose = hitResult.hitPose
                                                this@apply.rotation = Rotation(anchor.pose.rotation)
                                            }
                                            delay(50)
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "⏹️ Rotation Disabled")
                                    isRotationActive = false
                                    this.isScaleEditable = true
                                    this.isEditable = true
                                    this.isRotationEditable = false
                                    rotationJob?.cancel()
                                }
                            }
                        }



                        // ✅ Enable Depth Occlusion Mode
                        sceneView.depthEnabled = true
                        sceneView.isFocusable = true
                        sceneView.isDepthOcclusionEnabled = true
                        sceneView.addChild(modelNode)
                        Log.d(TAG, "✅ Model added to AR scene")
                    }
                }

            )

            // Improved searching text overlay
            if (!isPlaneDetected) {
                Text(
                    text = "Searching for surface...",
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Depth Map Overlay
            if (isDepthCaptureActive) {
                lastDepthBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Depth Map",
                        modifier = Modifier
                            .aspectRatio(depthMapAspectRatio)
                            .fillMaxSize()
                            .rotate(90f)
                    )
                }
            }
        }

        // Button Grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonAreaHeight)
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(6) { index ->  // For 6 buttons (3 rows of 2)
                when (index) {
                    0 -> Button(
                        onClick = { isDepthCaptureActive = !isDepthCaptureActive },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isDepthCaptureActive) "Stop Depth View" else "Show Depth View")
                    }
                    1 -> Button(
                        onClick = {
                            if (addedModels.isNotEmpty()) {
                                viewModel.saveLayoutWithModels("MyLayout", addedModels.toList())
                                Log.d(TAG, "💾 Layout saved with ${addedModels.size} models.")

                                // Clean up the active model after saving
                                activeModelNode?.let {
                                    it.isEditable = false
                                    it.isScaleEditable = false
                                    it.isRotationEditable = false
                                    it.selectionVisualizer = null  // Remove selection visualization
                                }

                                activeModelNode = null  // Clear the active model
                                isSaveButtonEnabled = false
                                isModelPlaced = false  // Allow adding a new model
                            } else {
                                Log.e(TAG, "❌ No models to save.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isSaveButtonEnabled
                    ) {
                        Text("Save Model")
                    }
                    2 -> Button(
                        onClick = {
                            showModelSelectionSheet = true
                            // ❌ Make saved models non-editable
                            activeModelNode?.let {
                                it.isEditable = true
                                it.isScaleEditable = true
                                it.isRotationEditable = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isModelPlaced // Disable if a model is placed
                    ) {
                        Text("Add Model")
                    }
                    3 -> Button(
                        onClick = {
                            activeModelNode?.let { node ->
                                node.selectionVisualizer = null
                                arSceneView.value?.removeChild(node)
                                // Find and remove only the specific model from addedModels
                                val iterator = addedModels.iterator()
                                while (iterator.hasNext()) {
                                    val model = iterator.next()
                                    if (model.posX == node.position.x &&
                                        model.posY == node.position.y &&
                                        model.posZ == node.position.z
                                    ) {
                                        iterator.remove()
                                        break
                                    }
                                }
                                activeModelNode = null
                                isModelPlaced = false
                                isSaveButtonEnabled = false
                                Log.d(TAG, "🗑️ Model deleted")
                            } ?: run {
                                Log.d(TAG, "⚠️ No model to delete")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = activeModelNode != null
                    ) {
                        Text("Delete Model")
                    }
                    4 -> Button(
                        onClick = { /* Button 5 action */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Button 5")
                    }
                    5 -> Button(
                        onClick = { navigate.navigateUp() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    ) {
                        Text("Done")
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = buttonAreaHeight + 16.dp) // Position above buttons
        )

        if (showModelSelectionSheet) {
            ModelSelectionBottomSheet(
                onModelSelected = { selectedModelName ->
                    selectedModel = selectedModelName
                    viewModel.selectModel(selectedModelName)
                    Log.d(TAG, "✅ Model selected from BottomSheet: $selectedModelName")

                    // ✅ Reset activeModelNode to ensure new model is editable
                    activeModelNode?.let {
                        it.isEditable = false
                        it.isScaleEditable = false
                        it.isRotationEditable = false
                    }

                    activeModelNode = null // Reset to make sure a new one is assigned
                    isModelPlaced = false // ✅ Allow model placement
                    showModelSelectionSheet = false
                },
                onDismiss = { showModelSelectionSheet = false }
            )
        }
    }
}



// Basic Wokring
//package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.util.Log
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.google.ar.core.Config
//import com.google.ar.core.Plane
//import com.google.ar.core.TrackingState
//import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
//import com.jsb.arhomerenovat.feature_ar_home.presentation.util.ModelSelectionBottomSheet
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.ArSceneView
//import io.github.sceneview.ar.arcore.position
//import io.github.sceneview.ar.arcore.quaternion
//import io.github.sceneview.ar.arcore.rotation
//import io.github.sceneview.ar.node.ArModelNode
//import io.github.sceneview.math.Position
//import io.github.sceneview.math.Rotation
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//
//private const val TAG = "ARDepthScreen"
//
//@Composable
//fun ARDepthEstimationScreen(
//    initialModelFileName: String,
//    viewModel: ARDepthEstimationViewModel = hiltViewModel()
//) {
//    var selectedModel by remember { mutableStateOf(initialModelFileName) }
//    var activeModelNode by remember { mutableStateOf<ArModelNode?>(null) }
//    var showModelSelectionSheet by remember { mutableStateOf(false) }
//    val arSceneView = remember { mutableStateOf<ArSceneView?>(null) }
//
//    var isWaitingForToggle by remember { mutableStateOf(false) }
//    var isRotationActive by remember { mutableStateOf(false) }
//    var rotationJob: Job? by remember { mutableStateOf(null) }
//
//    val addedModels = remember { mutableStateListOf<ModelEntity>() }
//    var isModelPlaced by remember { mutableStateOf(false) } // Restrict multiple model placements
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        ARScene(
//            modifier = Modifier.fillMaxSize(),
//            planeRenderer = true,
//            onCreate = { sceneView ->
//                arSceneView.value = sceneView
//                Log.d(TAG, "✅ AR Scene Created")
//            },
//            onSessionCreate = { session ->
//                Log.d(TAG, "✅ AR Session Created")
//
//                // ✅ Enable Geospatial & Depth Mode
//                val config = session.config.apply {
//                    geospatialMode = Config.GeospatialMode.ENABLED
//                    depthMode = Config.DepthMode.AUTOMATIC // ✅ Enable Depth Mode
//                }
//                session.configure(config)
//            },
//            onFrame = { arFrame ->
//                val trackables = arSession?.getAllTrackables(Plane::class.java) ?: emptyList()
//
//                for (plane in trackables) {
//                    if (plane.trackingState == TrackingState.TRACKING && plane.subsumedBy == null) {
//                        // ✅ Use only high-confidence planes
//                        if (plane.extentX < 0.5f || plane.extentZ < 0.5f) continue // 🔥 Ignore tiny planes
//                        if (plane.centerPose.translation[1] > 1.5f) continue // 🔥 Ignore planes above 1.5m (walls, ceilings)
//
//                        Log.d(TAG, "✅ Plane Detected: X=${plane.extentX}, Z=${plane.extentZ}")
//                    }
//                }
//            },
//            onTap = { hitResult ->
//                if (isModelPlaced) {
//                    Log.d(TAG, "⚠️ A model is already placed. Save it before adding another.")
//                    return@ARScene
//                }
//
//                val pose = hitResult.hitPose
//                val anchor = hitResult.createAnchor()
//                arSceneView.value?.let { sceneView ->
//                    val earth = sceneView.arSession?.earth
//                    if (earth == null || earth.trackingState != TrackingState.TRACKING) {
//                        Log.e(TAG, "❌ Earth is not tracking. Cannot place model.")
//                        return@let
//                    }
//
//                    val geospatialPose = earth.cameraGeospatialPose
//                    val latitude = geospatialPose.latitude
//                    val longitude = geospatialPose.longitude
//                    val altitude = geospatialPose.altitude
//                    var userIsTouching = false
//
//                    val modelNode = ArModelNode(sceneView.engine).apply {
//                        loadModelGlbAsync(
//                            glbFileLocation = selectedModel,
//                            scaleToUnits = 0.3f,
//                            onLoaded = {
//                                Log.d(TAG, "✅ Model loaded successfully: $selectedModel")
//
//                                this.anchor = anchor
//                                this.isVisible = true
//                                this.isEditable = true
//                                this.isRotationEditable = false
//                                this.isScaleEditable = true
//                                this.rotation = Rotation(pose.rotation)
//                                this.position = Position(pose.position)
//
//                                activeModelNode = this
//                                isModelPlaced = true // Restrict new model addition
//
//                                // ✅ Store model data with geospatial values
//                                addedModels.add(
//                                    ModelEntity(
//                                        layoutId = 0, // Will be updated when saving
//                                        modelName = selectedModel,
//                                        posX = pose.position.x,
//                                        posY = pose.position.y,
//                                        posZ = pose.position.z,
//                                        qx = pose.quaternion.x,
//                                        qy = pose.quaternion.y,
//                                        qz = pose.quaternion.z,
//                                        qw = pose.quaternion.w,
//                                        scaleX = 1.0f,
//                                        scaleY = 1.0f,
//                                        scaleZ = 1.0f,
//                                        latitude = latitude,
//                                        longitude = longitude,
//                                        altitude = altitude
//                                    )
//                                )
//
//                                Log.d(TAG, "🌍 Geospatial Data -> Lat: $latitude, Long: $longitude, Alt: $altitude")
//                            },
//                            onError = { error ->
//                                Log.e(TAG, "❌ Error loading model: ${error.message}")
//                            }
//                        )
//
//                        // 🔄 Disable Editing After Saving
//                        onDoubleTapEvent = listOf {
//                            if (isWaitingForToggle) return@listOf
//
//                            isWaitingForToggle = true
//                            CoroutineScope(Dispatchers.Main).launch {
//                                delay(300)
//                                isWaitingForToggle = false
//                            }
//
//                            if (!isRotationActive) {
//                                Log.d(TAG, "▶️ Rotation Enabled")
//                                isRotationActive = true
//                                this.isScaleEditable = false
//                                this.isEditable = false
//                                this.isRotationEditable = true
//
//                                rotationJob?.cancel()
//                                rotationJob = CoroutineScope(Dispatchers.Default).launch {
//                                    while (isActive && isRotationActive) {
//                                        arSceneView.value?.hitTest(
//                                            position = hitResult.hitPose.position,
//                                            plane = true,
//                                            depth = true,
//                                            instant = true
//                                        )?.let { hitResult ->
//                                            val newPose = hitResult.hitPose
//                                            this@apply.rotation = Rotation(anchor.pose.rotation)
//                                        }
//                                        delay(50)
//                                    }
//                                }
//                            } else {
//                                Log.d(TAG, "⏹️ Rotation Disabled")
//                                isRotationActive = false
//                                this.isScaleEditable = true
//                                this.isEditable = true
//                                this.isRotationEditable = false
//                                rotationJob?.cancel()
//                            }
//                        }
//                    }
//
//
//
//                    // ✅ Enable Depth Occlusion Mode
//                    sceneView.depthEnabled = true
//                    sceneView.isFocusable = true
//                    sceneView.isDepthOcclusionEnabled = true
//                    sceneView.addChild(modelNode)
//                    Log.d(TAG, "✅ Model added to AR scene")
//                }
//            }
//        )
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .padding(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        onClick = {
//                            if (addedModels.isNotEmpty()) {
//                                viewModel.saveLayoutWithModels("MyLayout", addedModels.toList())
//                                Log.d(TAG, "💾 Layout saved with ${addedModels.size} models.")
//
//                                // ❌ Make saved models non-editable
//                                activeModelNode?.let {
//                                    it.isEditable = false
//                                    it.isScaleEditable = false
//                                    it.isRotationEditable = false
//                                    it.onDoubleTapEvent = emptyList() // Disable further interaction
//                                }
//
//                                isModelPlaced = false // Allow adding a new model
//                            } else {
//                                Log.e(TAG, "❌ No models to save.")
//                            }
//                        }
//                    ) {
//                        Text("Save Model")
//                    }
//
//                    Button(
//                        onClick = {
//                            showModelSelectionSheet = true
//                            // ❌ Make saved models non-editable
//                            activeModelNode?.let {
//                                it.isEditable = true
//                                it.isScaleEditable = true
//                                it.isRotationEditable = true
//                            }
//                                  },
//                        enabled = !isModelPlaced // Disable if a model is placed
//                    ) {
//                        Text("Add Model")
//                    }
//                }
//
//                // ✅ Added Done Button (Implementation pending)
//                Button(
//                    onClick = { /* Implementation pending */ },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Done")
//                }
//            }
//        }
////
//        if (showModelSelectionSheet) {
//            ModelSelectionBottomSheet(
//                onModelSelected = { selectedModelName ->
//                    selectedModel = selectedModelName
//                    viewModel.selectModel(selectedModelName)
//                    Log.d(TAG, "✅ Model selected from BottomSheet: $selectedModelName")
//
//                    // ✅ Reset activeModelNode to ensure new model is editable
//                    activeModelNode?.let {
//                        it.isEditable = false
//                        it.isScaleEditable = false
//                        it.isRotationEditable = false
//                    }
//
//                    activeModelNode = null // Reset to make sure a new one is assigned
//                    isModelPlaced = false // ✅ Allow model placement
//                    showModelSelectionSheet = false
//                },
//                onDismiss = { showModelSelectionSheet = false }
//            )
//        }
//
//    }
//}