package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.*
import com.google.ar.core.exceptions.*

@Composable
fun ARCoreDepthView(context: Context) {
    var arSession by remember { mutableStateOf<Session?>(null) }

    // Initialize AR Session on the Main Thread
    LaunchedEffect(Unit) {
        try {
            val session = Session(context)
            val config = Config(session).apply {
                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    depthMode = Config.DepthMode.AUTOMATIC
                }
            }
            session.configure(config)
            arSession = session
        } catch (e: UnavailableException) {
            Log.e("ARCore", "AR session could not start: ${e.localizedMessage}")
        }
    }

    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).apply {
                val surfaceView = SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            arSession?.let { session ->
                                try {
                                    session.resume()
                                    visualizeDepth(this@apply, session)
                                } catch (e: CameraNotAvailableException) {
                                    Log.e("ARCore", "Camera not available: ${e.localizedMessage}")
                                }
                            }
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                        override fun surfaceDestroyed(holder: SurfaceHolder) {}
                    })
                }
                addView(surfaceView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    )
}

private fun visualizeDepth(surfaceView: SurfaceView, session: Session) {
    try {
        val frame = session.update()
        val depthImage = frame.acquireDepthImage16Bits()
        val depthData = processDepthData(depthImage)
        drawDepthOnSurface(surfaceView, depthData)
    } catch (e: Exception) {
        Log.e("ARCore", "Failed to get depth data: ${e.localizedMessage}")
    }
}

private fun processDepthData(depthImage: Image): FloatArray {
    val width = depthImage.width
    val height = depthImage.height
    val depthArray = FloatArray(width * height)

    val buffer = depthImage.planes[0].buffer
    buffer.rewind()

    for (i in 0 until width * height) {
        val depthMM = buffer.short.toInt() and 0xFFFF
        depthArray[i] = depthMM.toFloat()
    }

    depthImage.close()
    return depthArray
}

private fun drawDepthOnSurface(surfaceView: SurfaceView, depthData: FloatArray) {
    val canvas = surfaceView.holder.lockCanvas()
    if (canvas != null) {
        for (i in depthData.indices) {
            val depth = depthData[i]
            val color = when {
                depth < 500f -> 0xFF0000FF.toInt() // Blue for close objects
                depth < 1000f -> 0xFF00FF00.toInt() // Green for mid-range
                else -> 0xFFFF0000.toInt() // Red for far objects
            }
            canvas.drawColor(color)
        }
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}
