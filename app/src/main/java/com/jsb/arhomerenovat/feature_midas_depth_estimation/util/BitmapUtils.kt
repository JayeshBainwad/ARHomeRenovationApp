package com.jsb.arhomerenovat.feature_midas_depth_estimation.util

import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream

// Utility class for Bitmap-related operations
object BitmapUtils {

    /**
     * Rotates a Bitmap by the specified degrees.
     *
     * @param source The Bitmap to rotate.
     * @param degrees The degrees to rotate the Bitmap.
     * @return The rotated Bitmap.
     */
    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun byteBufferToBitmap( imageArray : FloatArray , imageDim : Int ) : Bitmap {
        val pixels = imageArray.map { it.toInt() }.toIntArray()
        val bitmap = Bitmap.createBitmap(imageDim, imageDim, Bitmap.Config.RGB_565 );
        for ( i in 0 until imageDim ) {
            for ( j in 0 until imageDim ) {
                val p = pixels[ i * imageDim + j ]
                bitmap.setPixel( j , i , Color.rgb( p , p , p ))
            }
        }
        return bitmap
    }

    /**
     * Converts an [Image] from the YUV_420_888 format to a [Bitmap].
     * The resulting Bitmap is rotated by the specified rotation degrees.
     *
     * @param image The input [Image].
     * @param rotationDegrees The degrees to rotate the resulting Bitmap.
     * @return The converted and rotated Bitmap.
     */
    fun imageToBitmap(image: Image, rotationDegrees: Int): Bitmap {
        // Extract YUV data from the Image
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // Convert NV21 format to YuvImage
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)

        // Convert YuvImage to JPEG and then to Bitmap
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val jpegBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

        // Rotate the Bitmap to correct orientation
        return rotateBitmap(bitmap, rotationDegrees.toFloat())
    }
}
