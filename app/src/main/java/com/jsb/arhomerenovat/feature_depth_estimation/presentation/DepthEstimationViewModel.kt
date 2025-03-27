package com.jsb.arhomerenovat.feature_depth_estimation.presentation

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jsb.arhomerenovat.presentation.PointCloudGenerator
import com.jsb.arhomerenovat.feature_depth_estimation.data.FrameAnalyser
import com.jsb.arhomerenovat.feature_depth_estimation.data.MiDASModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors

class DepthEstimationViewModel(private val app: Application) : AndroidViewModel(app) {

    private val cameraProviderFuture = ProcessCameraProvider.getInstance(app)
    private lateinit var frameAnalyser: FrameAnalyser
    private val _depthBitmap = MutableStateFlow<Bitmap?>(null)
    val depthBitmap: StateFlow<Bitmap?> = _depthBitmap

    private val _isDepthEstimationEnabled = MutableStateFlow(true)
    val isDepthEstimationEnabled: StateFlow<Boolean> = _isDepthEstimationEnabled

    private val pointCloudGenerator = PointCloudGenerator(
        fx = 1000f, fy = 1000f, cx = 500f, cy = 500f // Example values; replace with actual camera intrinsics
    )

    private val _pointCloud = MutableStateFlow<List<PointCloudGenerator.Point3D>>(emptyList())
    val pointCloud: StateFlow<List<PointCloudGenerator.Point3D>> = _pointCloud

    private var captureSingleFrame = false

    init {
        val depthModel = MiDASModel(app)
        frameAnalyser = FrameAnalyser(depthModel) { depthMap: Bitmap ->
            if (captureSingleFrame) {
                _depthBitmap.value = depthMap
                processDepthMap(depthMap) // Process depth map directly
                captureSingleFrame = false
            }
        }
    }

    fun captureSingleFrame() {
        captureSingleFrame = true
    }

    fun processDepthMap(depthMap: Bitmap) {
        val points = pointCloudGenerator.generatePointCloud(depthMap)
        _pointCloud.value = points
    }

    fun toggleDepthEstimation() {
        _isDepthEstimationEnabled.value = !_isDepthEstimationEnabled.value
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().apply {
                    surfaceProvider = previewView.surfaceProvider
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser)
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                Log.e("DepthEstimationViewModel", "Error during camera initialization: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(app))
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DepthEstimationViewModel(application) as T
                }
            }
        }
    }
}
