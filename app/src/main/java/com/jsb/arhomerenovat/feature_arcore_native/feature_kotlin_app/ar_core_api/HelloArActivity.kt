// HelloArActivity.kt
package com.jsb.arhomerenovat.feature_arcore_native.feature_kotlin_app.ar_core_api

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import android.view.GestureDetector
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.core.view.GestureDetectorCompat
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*
import com.jsb.arhomerenovat.feature_arcore_native.feature_kotlin_app.ar_core_api.components.ARSettingsButton
import com.jsb.arhomerenovat.feature_arcore_native.feature_kotlin_app.life_cycle_helper.ARCoreSessionLifecycleHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.CameraPermissionHelper
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.DepthSettings
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.FullScreenHelper
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.InstantPlacementSettings
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.helpers.TapHelper
import kotlin.math.atan2
import com.jsb.arhomerenovat.R
import com.jsb.arhomerenovat.feature_arcore_native.feature_java_helper.samplerender.SampleRender

class HelloArActivity : ComponentActivity() {
    private var glSurfaceView: GLSurfaceView? = null
    private var isRendererInitialized = false
//    var isFirstTap = true

    companion object {
        private const val TAG = "HelloArActivityARCore"
        fun logD(msg: String) = Log.d(TAG, msg)
        fun logE(msg: String, e: Exception? = null) = Log.e(TAG, msg, e)
    }

    lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
    lateinit var renderer: HelloArRenderer
    lateinit var tapHelper: TapHelper

    val instantPlacementSettings = InstantPlacementSettings()
    val depthSettings = DepthSettings()

    lateinit var gestureDetector: GestureDetectorCompat
    lateinit var scaleGestureDetector: ScaleGestureDetector
    var scaleFactor = 1f
    var rotationAngle = 0f
    var deltaX = 0f
    var deltaY = 0f
    var lastTouchX = 0f
    var lastTouchY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD("onCreate() started")

        // 1. Initialize ARCore session helper
        arCoreSessionHelper = ARCoreSessionLifecycleHelper(this).apply {
            exceptionCallback = { exception ->
                val message = when (exception) {
                    is UnavailableUserDeclinedInstallationException ->
                        "Please install Google Play Services for AR"
                    is UnavailableApkTooOldException -> "Please update ARCore"
                    is UnavailableSdkTooOldException -> "Please update this app"
                    is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
                    is CameraNotAvailableException -> "Camera not available. Try restarting the app."
                    else -> "Failed to create AR session: $exception"
                }
                logE("ARCore exception: $message", exception)
                Toast.makeText(this@HelloArActivity, message, Toast.LENGTH_LONG).show()
            }
            beforeSessionResume = ::configureSession
        }
        lifecycle.addObserver(arCoreSessionHelper)
        logD("ARCoreSessionHelper initialized")

        // 2. Initialize renderer
        renderer = HelloArRenderer(this)
        lifecycle.addObserver(renderer)
        logD("HelloArRenderer initialized")

        // 3. Initialize other components
        tapHelper = TapHelper(this)
        depthSettings.onCreate(this)
        instantPlacementSettings.onCreate(this)
        logD("Other components initialized")

        // Inside onCreate(), after initializing tapHelper:
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                deltaX = -distanceX * 0.01f  // Adjust sensitivity
                deltaY = -distanceY * 0.01f
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.1f, 5.0f)  // Limit scale range
                return true
            }
        })

        // 4. Set Compose content
        setContent {
            logD("Composition started")
            ARCoreView(
                activity = this@HelloArActivity,
                renderer = renderer,
                tapHelper = tapHelper,
                onViewAvailable = { view ->
                    logD("onViewAvailable callback triggered")
                    if (!isRendererInitialized) {
                        logD("First time view available, storing reference")
                        glSurfaceView = view
                        isRendererInitialized = true
                    } else {
                        logD("View already initialized, ignoring duplicate")
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        logD("onResume() called")
        glSurfaceView?.let {
            logD("Calling onResume() on GLSurfaceView")
            it.onResume()
        } ?: logD("No GLSurfaceView reference available")
    }

    override fun onPause() {
        super.onPause()
        logD("onPause() called")
        glSurfaceView?.let {
            logD("Calling onPause() on GLSurfaceView")
            it.onPause()
        } ?: logD("No GLSurfaceView reference available")
    }

    override fun onDestroy() {
        super.onDestroy()
        logD("onDestroy() called")
        glSurfaceView?.apply {
            logD("Cleaning up GLSurfaceView")
            setRenderer(null)
            onPause()
        }
        glSurfaceView = null
        isRendererInitialized = false
    }

    private fun configureSession(session: Session) {
        logD("Configuring ARCore session")
        session.configure(
            session.config.apply {
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                depthMode = if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    logD("Using AUTOMATIC depth mode")
                    Config.DepthMode.AUTOMATIC
                } else {
                    logD("Depth mode not supported, using DISABLED")
                    Config.DepthMode.DISABLED
                }
                instantPlacementMode = if (instantPlacementSettings.isInstantPlacementEnabled) {
                    logD("Using LOCAL_Y_UP instant placement")
                    Config.InstantPlacementMode.LOCAL_Y_UP
                } else {
                    logD("Instant placement disabled")
                    Config.InstantPlacementMode.DISABLED
                }
            }
        )
    }

    @Deprecated("Deprecated in favor of Activity Result API")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                this,
                "Camera permission is needed to run this application",
                Toast.LENGTH_LONG
            ).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARCoreView(
    activity: HelloArActivity,
    renderer: HelloArRenderer,
    tapHelper: TapHelper,
    onViewAvailable: (GLSurfaceView) -> Unit
) {
    val context = LocalContext.current
    var glSurfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }
    var rendererInitialized by remember { mutableStateOf(false) }
    var showDepthDialog by remember { mutableStateOf(false) }

    // Show depth dialog only once when first anchor is added
    LaunchedEffect(renderer.wrappedAnchors.size) {
        if (renderer.wrappedAnchors.size == 1) {
            val session = activity.arCoreSessionHelper.session
            if (session?.isDepthModeSupported(Config.DepthMode.AUTOMATIC) == true &&
                activity.depthSettings.shouldShowDepthEnableDialog()) {
                showDepthDialog = true
            }
        }
    }

    if (showDepthDialog) {
        BasicAlertDialog(
            onDismissRequest = {
                showDepthDialog = false
                activity.depthSettings.setUseDepthForOcclusion(false)
            },
            modifier = Modifier.wrapContentSize(),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.options_title_with_depth),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.depth_use_explanation),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                showDepthDialog = false
                                activity.depthSettings.setUseDepthForOcclusion(false)
                            }
                        ) {
                            Text(stringResource(R.string.button_text_disable_depth))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                showDepthDialog = false
                                activity.depthSettings.setUseDepthForOcclusion(true)
                            }
                        ) {
                            Text(stringResource(R.string.button_text_enable_depth))
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                HelloArActivity.logD("Creating new GLSurfaceView instance")
                GLSurfaceView(ctx).apply {
                    glSurfaceView = this
                    HelloArActivity.logD("GLSurfaceView created with hash: ${hashCode()}")

                    // Configure GLSurfaceView
                    setEGLContextClientVersion(3)
                    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                    preserveEGLContextOnPause = true
                    HelloArActivity.logD("GLSurfaceView configured")

                    if (!rendererInitialized) {
                        HelloArActivity.logD("Initializing SampleRender and Renderer")
                        try {
                            // Initialize SampleRender only once
                            val sampleRender = SampleRender(this, renderer, context.assets)

                            setRenderer(object : GLSurfaceView.Renderer {
                                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                                    HelloArActivity.logD("onSurfaceCreated called")
                                    renderer.onSurfaceCreated(sampleRender)
                                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                                    rendererInitialized = true
                                    onViewAvailable(this@apply)
                                }

                                override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                                    HelloArActivity.logD("onSurfaceChanged: $width x $height")
                                    renderer.onSurfaceChanged(sampleRender, width, height)
                                }

                                override fun onDrawFrame(gl: GL10?) {
                                    renderer.onDrawFrame(sampleRender)
                                }
                            })
                            HelloArActivity.logD("Renderer set successfully")
                        } catch (e: Exception) {
                            HelloArActivity.logE("Failed to initialize renderer", e)
                        }
                    } else {
                        HelloArActivity.logD("Renderer already initialized, skipping")
                    }

                    setOnTouchListener { v, event ->
                        // Process scale and rotation first
                        activity.scaleGestureDetector.onTouchEvent(event)

                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // Record initial touch position
                                activity.lastTouchX = event.x
                                activity.lastTouchY = event.y
                            }
                            MotionEvent.ACTION_MOVE -> {
                                if (event.pointerCount == 1) {
                                    // Calculate movement delta (translation)
                                    val currentX = event.x
                                    val currentY = event.y
                                    activity.deltaX += (currentX - activity.lastTouchX) * 0.01f
                                    activity.deltaY += (currentY - activity.lastTouchY) * 0.01f
                                    activity.lastTouchX = currentX
                                    activity.lastTouchY = currentY
                                }
                                else if (event.pointerCount == 2) {
                                    // Rotation logic (unchanged)
                                    val newX = event.getX(0) - event.getX(1)
                                    val newY = event.getY(0) - event.getY(1)
                                    activity.rotationAngle += Math.toDegrees(
                                        atan2(newY.toDouble(), newX.toDouble()) -
                                                atan2(activity.lastTouchY.toDouble(), activity.lastTouchX.toDouble())
                                    ).toFloat()
                                    activity.lastTouchX = newX
                                    activity.lastTouchY = newY
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                // No need to reset deltas here - keep the transformed position
                                v.performClick()
                            }
                        }

                        activity.tapHelper.onTouch(v, event)
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                HelloArActivity.logD("AndroidView update called for view: ${view.hashCode()}")
            }
        )

        // Add this reset button
        Button(
            onClick = {
                activity.scaleFactor = 1f
                activity.rotationAngle = 0f
                activity.deltaX = 0f
                activity.deltaY = 0f
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp) // Adjust to avoid overlapping with ARSettingsButton
        ) {
            Text("Reset Transformations")
        }

        ARSettingsButton(
            activity = activity,
            depthSettings = activity.depthSettings
        )
    }
}
//@Deprecated("Deprecated in favor of Activity Result API")
//override fun onRequestPermissionsResult(
//    requestCode: Int,
//    permissions: Array<String>,
//    grantResults: IntArray
//) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    if (!CameraPermissionHelper.hasCameraPermission(this)) {
//        Toast.makeText(
//            this,
//            "Camera permission is needed to run this application",
//            Toast.LENGTH_LONG
//        ).show()
//        if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
//            CameraPermissionHelper.launchPermissionSettings(this)
//        }
//        finish()
//    }
//}
//
//override fun onWindowFocusChanged(hasFocus: Boolean) {
//    super.onWindowFocusChanged(hasFocus)
//    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
//}