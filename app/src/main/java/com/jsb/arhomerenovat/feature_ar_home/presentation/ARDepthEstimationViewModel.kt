package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.core.Frame
import com.google.ar.core.exceptions.NotYetAvailableException
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import com.jsb.arhomerenovat.feature_depth_estimation.data.IntrinsicParameters
import com.jsb.arhomerenovat.feature_depth_estimation.data.MiDASModel
import com.jsb.arhomerenovat.feature_depth_estimation.data.Point3D
import com.jsb.arhomerenovat.presentation.PointCloudGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.sceneview.ar.ArSceneView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "ARDepthScreen"

@HiltViewModel
class ARDepthEstimationViewModel @Inject constructor(
    private val repository: ModelRepository,
    private val miDASModel: MiDASModel,
    private val pointCloudGenerator: PointCloudGenerator,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // AR Scene View reference
    private var arSceneView: ArSceneView? = null

    // Depth Map State
    private val _depthBitmap = MutableStateFlow<Bitmap?>(null)
    val depthBitmap: StateFlow<Bitmap?> = _depthBitmap

    // Point Cloud State
    private val _pointCloud = MutableStateFlow<List<Point3D>>(emptyList())
    val pointCloud: StateFlow<List<Point3D>> = _pointCloud

    // Camera intrinsics (adjust these values based on your camera calibration)
    private val intrinsicParams = IntrinsicParameters(
        fx = 1000f,  // Focal length in pixels (x-axis)
        fy = 1000f,  // Focal length in pixels (y-axis)
        cx = 500f,   // Principal point x-coordinate
        cy = 500f    // Principal point y-coordinate
    )

    fun setArSceneView(sceneView: ArSceneView) {
        arSceneView = sceneView
    }

    fun captureDepthFrame(onComplete: (Boolean) -> Unit) {
        val currentFrame = arSceneView?.currentFrame?.frame ?: run {
            onComplete(false)
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Convert ARCore frame to bitmap
                val bitmap = convertArFrameToBitmap(currentFrame)

                // Process with MiDaS to get depth values
                val depthValues = miDASModel.getDepthMap(bitmap)

                // Convert depth values to visual Bitmap
                val depthBitmap = convertDepthToBitmap(depthValues)

                // Generate point cloud from depth values
                val pointCloud = miDASModel.generatePointCloud(depthValues, intrinsicParams)

                // Update UI
                withContext(Dispatchers.Main) {
                    _depthBitmap.value = depthBitmap
                    _pointCloud.value = pointCloud
                    onComplete(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Depth capture failed", e)
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    private fun convertDepthToBitmap(depthValues: FloatArray): Bitmap {
        val width = miDASModel.inputImageDim
        val height = miDASModel.inputImageDim
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Find min and max depth for normalization
        val maxDepth = depthValues.maxOrNull() ?: 1f
        val minDepth = depthValues.minOrNull() ?: 0f
        val range = maxDepth - minDepth

        // Convert depth values to grayscale bitmap
        for (y in 0 until height) {
            for (x in 0 until width) {
                val depth = depthValues[y * width + x]
                // Normalize depth to 0-255 range
                val normalized = ((depth - minDepth) / range * 255).toInt().coerceIn(0, 255)
                val color = android.graphics.Color.rgb(normalized, normalized, normalized)
                bitmap.setPixel(x, y, color)
            }
        }

        return bitmap
    }

    private fun convertArFrameToBitmap(frame: Frame): Bitmap {
        val image = try {
            frame.acquireCameraImage()
        } catch (e: NotYetAvailableException) {
            throw IllegalStateException("Camera image not available")
        }

        return try {
            // Convert YUV_420_888 to RGB bitmap
            val width = image.width
            val height = image.height
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            // Copy Y channel
            yBuffer.get(nv21, 0, ySize)

            // Copy VU channels (swapped from UV)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            // Convert to RGB
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            convertYUV420ToRGB(nv21, width, height, bitmap)
            bitmap
        } finally {
            image.close()
        }
    }

    private fun convertYUV420ToRGB(yuv420: ByteArray, width: Int, height: Int, bitmap: Bitmap) {
        val pixels = IntArray(width * height)
        val frameSize = width * height

        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = (0xff and yuv420[j * width + i].toInt())
                val u = (0xff and yuv420[frameSize + (j shr 1) * width + (i shr 1) * 2 + 0].toInt())
                val v = (0xff and yuv420[frameSize + (j shr 1) * width + (i shr 1) * 2 + 1].toInt())

                pixels[j * width + i] = yuvToRgb(y, u, v)
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun yuvToRgb(y: Int, u: Int, v: Int): Int {
        var r = y + (1.370705 * (v - 128)).toInt()
        var g = y - (0.698001 * (v - 128)).toInt() - (0.337633 * (u - 128)).toInt()
        var b = y + (1.732446 * (u - 128)).toInt()

        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)

        return -0x1000000 or (r shl 16) or (g shl 8) or b
    }

    fun clearDepthMap() {
        _depthBitmap.value = null
    }
}




//package com.jsb.arhomerenovat.feature_ar_home.presentation
//
//import android.app.Application
//import android.content.Context
//import android.graphics.Bitmap
//import android.util.Log
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.google.ar.core.Frame
//import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
//import com.jsb.arhomerenovat.feature_ar_home.data.repository.ModelRepositoryImpl
//import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
//import com.jsb.arhomerenovat.feature_depth_estimation.data.FrameAnalyser
//import com.jsb.arhomerenovat.feature_depth_estimation.data.MiDASModel
//import com.jsb.arhomerenovat.feature_depth_estimation.presentation.DepthEstimationViewModel
//import com.jsb.arhomerenovat.presentation.PointCloudGenerator
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import java.util.concurrent.Executors
//import javax.inject.Inject
//
//private const val TAG = "ARDepthScreen"
//
//@HiltViewModel
//class ARDepthEstimationViewModel @Inject constructor(
//    private val repository: ModelRepository,
//    private val miDASModel: MiDASModel,
//    private val pointCloudGenerator: PointCloudGenerator,
//    @ApplicationContext private val context: Context
//) : ViewModel() {
//
//    /** Holds currently selected 3D model */
//    private val _selectedModel = MutableStateFlow<String?>(null)
//    val selectedModel: StateFlow<String?> = _selectedModel
//
//    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
//    private lateinit var frameAnalyser: FrameAnalyser
//    private val _depthBitmap = MutableStateFlow<Bitmap?>(null)
//    val depthBitmap: StateFlow<Bitmap?> = _depthBitmap
//
//    private val _isDepthEstimationEnabled = MutableStateFlow(true)
//    val isDepthEstimationEnabled: StateFlow<Boolean> = _isDepthEstimationEnabled
//
//    private val _pointCloud = MutableStateFlow<List<PointCloudGenerator.Point3D>>(emptyList())
//    val pointCloud: StateFlow<List<PointCloudGenerator.Point3D>> = _pointCloud
//
//    private var captureSingleFrame = false
//
//    init {
//        frameAnalyser = FrameAnalyser(miDASModel) { depthMap: Bitmap ->
//            if (captureSingleFrame) {
//                _depthBitmap.value = depthMap
//                processDepthMap(depthMap)
//                captureSingleFrame = false
//            }
//        }
//    }
//
//    fun captureSingleFrame() {
//        captureSingleFrame = true
//    }
//
//    fun processDepthMap(depthMap: Bitmap) {
//        val points = pointCloudGenerator.generatePointCloud(depthMap)
//        _pointCloud.value = points
//    }
//
//    fun toggleDepthEstimation() {
//        _isDepthEstimationEnabled.value = !_isDepthEstimationEnabled.value
//    }
//
//    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
//        cameraProviderFuture.addListener({
//            try {
//                val cameraProvider = cameraProviderFuture.get()
//                val preview = Preview.Builder().build().apply {
//                    surfaceProvider = previewView.surfaceProvider
//                }
//
//                val analysis = ImageAnalysis.Builder()
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//                    .also {
//                        it.setAnalyzer(Executors.newSingleThreadExecutor(), frameAnalyser)
//                    }
//
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner,
//                    CameraSelector.DEFAULT_BACK_CAMERA,
//                    preview,
//                    analysis
//                )
//            } catch (e: Exception) {
//                Log.e("DepthEstimationViewModel", "Error during camera initialization: ${e.message}", e)
//            }
//        }, ContextCompat.getMainExecutor(context))
//    }
//
//    fun selectModel(modelName: String) {
//        _selectedModel.value = modelName
//        Log.d(TAG, "✅ Model selected: $modelName")
//    }
//
//    fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>) {
//        if (layoutName.isBlank()) {
//            Log.e(TAG, "❌ Error: Layout name cannot be empty!")
//            return
//        }
//        if (models.isEmpty()) {
//            Log.e(TAG, "❌ Error: At least one model is required!")
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                repository.saveLayoutWithModels(layoutName, models)
//
//                Log.d(TAG, "💾 Layout '$layoutName' saved with ${models.size} models.")
//
//                models.forEach { model ->
//                    Log.d(TAG, "📌 Model Saved -> ID: ${model.layoutId}, Name: ${model.modelName}, " +
//                            "Position: (${model.posX}, ${model.posY}, ${model.posZ}), " +
//                            "Rotation: (${model.qx}, ${model.qy}, ${model.qz}, ${model.qw}), " +
//                            "🌍 Geospatial Location: (Lat: ${model.latitude}, Long: ${model.longitude}, Alt: ${model.altitude})"
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "❌ Error saving layout: ${e.message}", e)
//            }
//        }
//    }
//
//    companion object {
//        private const val TAG = "ARDepthEstimationVM"
//    }
//}