package com.jsb.arhomerenovat.feature_depth_estimation.data

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.jsb.arhomerenovat.feature_depth_estimation.data.util.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FrameAnalyser(
    private val depthEstimationModel: MiDASModel,
    private val onDepthMapComputed: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var isFrameProcessing = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (isFrameProcessing) {
            image.close()
            return
        }
        isFrameProcessing = true

        image.image?.let { img ->
            val bitmap = BitmapUtils.imageToBitmap(img, image.imageInfo.rotationDegrees)
            image.close()

            CoroutineScope(Dispatchers.Default).launch {
                val depthMap = depthEstimationModel.getDepthMap(bitmap) // FloatArray
                val depthBitmap = BitmapUtils.byteBufferToBitmap(depthMap, 256) // Convert to Bitmap

                withContext(Dispatchers.Main) {
                    isFrameProcessing = false
                    onDepthMapComputed(depthBitmap) // Pass the Bitmap
                }
            }
        } ?: image.close()
    }
}