package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.core.Frame
import com.google.ar.core.exceptions.NotYetAvailableException
import com.jsb.arhomerenovat.feature_ar_home.data.local.LayoutWithModels
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import com.jsb.arhomerenovat.feature_midas_depth_estimation.data.IntrinsicParameters
import com.jsb.arhomerenovat.feature_midas_depth_estimation.data.MiDASModel
import com.jsb.arhomerenovat.feature_midas_depth_estimation.data.Point3D
import com.jsb.arhomerenovat.feature_midas_depth_estimation.point_cloud_generator.PointCloudGenerator
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

    /** Holds currently selected 3D model */
    private val _selectedModel = MutableStateFlow<String?>(null)
    val selectedModel: StateFlow<String?> = _selectedModel

    // ARDepthEstimationViewModel.kt (partial update)
    private val _savedLayouts = MutableStateFlow<List<LayoutWithModels>>(emptyList())
    val savedLayouts: StateFlow<List<LayoutWithModels>> = _savedLayouts

    private val _loadedModels = MutableStateFlow<List<ModelEntity>>(emptyList())
    val loadedModels: StateFlow<List<ModelEntity>> = _loadedModels

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



    fun saveLayoutWithModels(layoutName: String, models: List<ModelEntity>) {
        if (layoutName.isBlank()) {
            Log.e(TAG, "❌ Error: Layout name cannot be empty!")
            return
        }
        if (models.isEmpty()) {
            Log.e(TAG, "❌ Error: At least one model is required!")
            return
        }

        viewModelScope.launch {
            try {
                repository.saveLayoutWithModels(layoutName, models)
                Log.d(TAG, "💾 Layout '$layoutName' saved with ${models.size} models.")

                models.forEach { model ->
                    Log.d(TAG, "📌 Model Saved -> ID: ${model.layoutId}, Name: ${model.modelName}, " +
                            "Position: (${model.posX}, ${model.posY}, ${model.posZ}), " +
                            "Rotation: (${model.qx}, ${model.qy}, ${model.qz}, ${model.qw}), " +
                            "🌍 Geospatial Location: (Lat: ${model.latitude}, Long: ${model.longitude}, Alt: ${model.altitude})"
                    )
                }

                // Refresh the layouts list
                loadAllLayouts()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error saving layout: ${e.message}", e)
            }
        }
    }

    fun loadAllLayouts() {
        viewModelScope.launch {
            repository.getAllLayouts().collect { layouts ->
                _savedLayouts.value = layouts
            }
        }
    }

    fun loadModelsForLayout(layoutId: Int) {
        viewModelScope.launch {
            _loadedModels.value = repository.getModelsForLayout(layoutId)
        }
    }

    fun deleteLayout(layoutId: Int) {
        viewModelScope.launch {
            repository.deleteLayout(layoutId)
            loadAllLayouts() // Refresh the list after deletion
        }
    }

    fun selectModel(modelName: String) {
        _selectedModel.value = modelName
        Log.d(TAG, "✅ Model selected: $modelName")
    }
}