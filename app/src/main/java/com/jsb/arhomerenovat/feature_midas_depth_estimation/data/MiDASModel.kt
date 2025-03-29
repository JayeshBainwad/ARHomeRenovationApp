package com.jsb.arhomerenovat.feature_midas_depth_estimation.data

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat

class MiDASModel(context: Context) {

    private val modelFileName = "depth_model.tflite"
    private var interpreter: Interpreter
    val inputImageDim = 256
    private val mean = floatArrayOf(123.675f, 116.28f, 103.53f)
    private val std = floatArrayOf(58.395f, 57.12f, 57.375f)

    private val inputProcessor = ImageProcessor.Builder()
        .add(ResizeOp(inputImageDim, inputImageDim, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp(mean, std))
        .build()

    private val outputProcessor: (TensorBuffer) -> TensorBuffer = { input ->
        MinMaxScalingOp().apply(input)
    }


    init {
        val options = Interpreter.Options().apply {
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
            } else {
                numThreads = 4
            }
        }
        interpreter = Interpreter(FileUtil.loadMappedFile(context, modelFileName), options)
    }

    fun getDepthMap(inputBitmap: Bitmap): FloatArray {
        val inputTensor = TensorImage.fromBitmap(inputBitmap).let {
            inputProcessor.process(it)
        }

        val outputTensor = TensorBufferFloat.createFixedSize(
            intArrayOf(inputImageDim, inputImageDim, 1), DataType.FLOAT32
        )

        interpreter.run(inputTensor.buffer, outputTensor.buffer)
        val processedOutput = outputProcessor(outputTensor)
        return processedOutput.floatArray
    }

    fun generatePointCloud(depthMap: FloatArray, intrinsicParams: IntrinsicParameters): List<Point3D> {
        val pointCloud = mutableListOf<Point3D>()
        val width = inputImageDim
        val height = inputImageDim

        for (v in 0 until height) {
            for (u in 0 until width) {
                val z = depthMap[v * width + u]
                val x = (u - intrinsicParams.cx) * z / intrinsicParams.fx
                val y = (v - intrinsicParams.cy) * z / intrinsicParams.fy
                pointCloud.add(Point3D(x, y, z))
            }
        }
        return pointCloud
    }

    private class MinMaxScalingOp {
        fun apply(input: TensorBuffer): TensorBuffer {
            val values = input.floatArray
            val max = values.maxOrNull()!!
            val min = values.minOrNull()!!
            for (i in values.indices) {
                var scaled = (((values[i] - min) / (max - min)) * 255).toInt()
                if (scaled < 0) scaled += 255
                values[i] = scaled.toFloat()
            }
            return TensorBufferFloat.createFixedSize(input.shape, DataType.FLOAT32).apply {
                loadArray(values)
            }
        }
    }
}

data class IntrinsicParameters(
    val fx: Float,
    val fy: Float,
    val cx: Float,
    val cy: Float
)

data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float
)
