package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.util.Log
//import android.view.MotionEvent
//import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import com.google.ar.core.*
//import io.github.sceneview.ar.ARScene
//import io.github.sceneview.ar.ARSceneView
//import io.github.sceneview.collision.HitResult
//import io.github.sceneview.node.ModelNode
//import io.github.sceneview.rememberEngine
//import io.github.sceneview.rememberModelLoader
//import io.github.sceneview.rememberScene
//
//private const val TAG = "ARCoreView"
//
@Composable
fun ARCoreView() {
//    val context = LocalContext.current
//    val engine = rememberEngine()
//    val modelLoader = rememberModelLoader(engine)
//    val scene = rememberScene(engine)
//    val arSceneView = remember { mutableStateOf<ARSceneView?>(null) }
//
//    ARScene(
//        modifier = Modifier.fillMaxSize(),
//        engine = engine,
//        scene = scene,
//        modelLoader = modelLoader,
//        planeRenderer = true, // ✅ Enables plane detection
//        sessionConfiguration = { session, config ->
//            config.depthMode = if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
//                Config.DepthMode.AUTOMATIC
//            else Config.DepthMode.DISABLED
//            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//        },
//        onViewCreated = {
//            arSceneView.value = this // ✅ Store the ARSceneView reference
//        },
//        onTouchEvent = { e: MotionEvent, hitResult: HitResult? ->
//            Log.d(TAG, "Touch event detected: ${e.action}")
//
//            arSceneView.value?.let { sceneView ->
//                if (hitResult != null) {
//                    Log.d(TAG, "HitResult detected at: ${hitResult.worldPosition}")
//
//                    val modelNode = ModelNode(
//                        modelInstance = modelLoader.createModelInstance(
//                            assetFileLocation = "android robot.glb"
//                        ),
//                        scaleToUnits = 0.3f
//                    ).apply {
//                        worldPosition = hitResult.worldPosition // ✅ Set position based on hit result
//                    }
//
//                    sceneView.addChildNode(modelNode) // ✅ Add model to the AR scene
//                    Log.d(TAG, "Model placed at: ${modelNode.worldPosition}")
//                } else {
//                    Log.d(TAG, "HitResult is null")
//                }
//            }
//            true
//        }
//    )
}












//package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import com.google.ar.core.*
//import io.github.sceneview.ar.ARSceneView
//import io.github.sceneview.ar.arcore.ARSession
//import io.github.sceneview.rememberEngine
//import io.github.sceneview.rememberModelLoader
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//private const val TAG = "ARCoreView"
//private const val LOG_INTERVAL_MS = 3000L  // Log every 3 seconds
//private var lastLogTime = 0L  // Track last log time
//
//@Composable
//fun ARCoreView() {
//    val context = LocalContext.current
//    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
//    val coroutineScope = rememberCoroutineScope()
//
//    // An Engine instance main function is to keep track of all resources created by the user and manage
//    // the rendering thread as well as the hardware renderer.
//    // To use filament, an Engine instance must be created first.
//    val engine = rememberEngine()
//
//    // Consumes a blob of glTF 2.0 content (either JSON or GLB) and produces a [Model] object, which is
//    // a bundle of Filament textures, vertex buffers, index buffers, etc.
//    // A [Model] is composed of 1 or more [ModelInstance] objects which contain entities and components.
//    val modelLoader = rememberModelLoader(engine)
//
//
//    var arSession by remember { mutableStateOf<ARSession?>(null) }
//    var isArCoreSupported by remember { mutableStateOf(false) }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            coroutineScope.launch(Dispatchers.Main) {
//                initializeARSession(context) { session, supported ->
//                    arSession = session
//                    isArCoreSupported = supported
//                }
//            }
//        } else {
//            Log.e(TAG, "Camera permission denied")
//        }
//    }
//
//    // Initialize AR Session if permission is granted
//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            initializeARSession(context) { session, supported ->
//                arSession = session
//                isArCoreSupported = supported
//            }
//        } else {
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    if (!isArCoreSupported) {
//        Text("ARCore is not supported on this device")
//    } else if (arSession == null) {
//        Text("Initializing ARCore...")
//    } else {
//        AndroidView(
//            factory = {
//                ARSceneView(
//                    context = context,
//                    sharedLifecycle = lifecycleOwner.lifecycle
//                ).apply {
//                    onSessionCreated = { Log.d(TAG, "AR Session Created") }
//
//                    sessionConfiguration = { _, config ->
//                        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
//                        config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
//                    }
//
//                    onSessionUpdated = { _, frame ->
//                        val currentTime = System.currentTimeMillis()
//
//                        val camera = frame.camera
//                        if (camera.trackingState == TrackingState.TRACKING) {
//                            frame.getUpdatedTrackables(Plane::class.java).forEach { plane ->
//                                if (plane.trackingState == TrackingState.TRACKING && plane.subsumedBy == null) {
//                                    val pose = plane.centerPose
//                                    if (currentTime - lastLogTime > LOG_INTERVAL_MS) {
//                                        Log.d(TAG, "Detected Plane at Pose: ${pose.translation.contentToString()}")
//                                        lastLogTime = currentTime
//                                    }
//
////                                    // Place 3D model when a plane is found
////                                    val hitResult = frame.hitTest(
////                                        pose.qx(),
////                                        pose.qx()
////                                    ).firstOrNull()
////                                    hitResult?.let {
////                                        coroutineScope.launch {
////                                            modelLoader.loadModelInstance(
////                                                "android robot.glb"
////                                            )
////                                        }
//////                                        place3DModel(it.hitPose, this, coroutineScope)
////                                        Log.d(TAG, "Placed 3D Model at: ${it.hitPose.translation.contentToString()}")
////                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
//
//// Initialize AR session
//private fun initializeARSession(context: Context, onResult: (ARSession?, Boolean) -> Unit) {
//    try {
//        val availability = ArCoreApk.getInstance().checkAvailability(context)
//        if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
//            val session = ARSession(
//                context = context,
//                onResumed = {},
//                onPaused = {},
//                onConfigChanged = { session: Session, config: Config -> }
//                )
//            onResult(session, true)
//        } else {
//            Log.e(TAG, "ARCore not supported on this device")
//            onResult(null, false)
//        }
//    } catch (e: Exception) {
//        Log.e(TAG, "Error initializing ARCore: ${e.localizedMessage}")
//        onResult(null, false)
//    }
//}

// Function to place 3D object at a given pose
// Function to place 3D object at a given pose
//private fun place3DModel(pose: Pose, sceneView: ARSceneView, coroutineScope: CoroutineScope) {
//    val context = sceneView.context
//    val modelPath = "models/android robot.glb" // Ensure correct asset path
//
//    coroutineScope.launch(Dispatchers.IO) {
//        try {
//            val modelInstance = ModelLoader.loadModelInstanceAsync(context, modelPath).await() // Asynchronous loading
//
//            modelInstance?.let {
//                val modelNode = ModelNode(it).apply {
//                    localPosition = pose.position
//                    localRotation = pose.rotation
//                }
//
//                sceneView.addChildNode(modelNode)
//                Log.d(TAG, "3D Model placed successfully at: ${pose.translation.contentToString()}")
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error loading 3D model: ${e.localizedMessage}")
//        }
//    }
//}
//
