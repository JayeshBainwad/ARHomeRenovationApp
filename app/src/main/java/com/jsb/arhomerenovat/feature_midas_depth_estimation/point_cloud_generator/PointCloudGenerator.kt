package com.jsb.arhomerenovat.feature_midas_depth_estimation.point_cloud_generator

import android.graphics.Bitmap
// For now not in use
class PointCloudGenerator(
    private val fx: Float, // Focal length in x-direction
    private val fy: Float, // Focal length in y-direction
    private val cx: Float, // Principal point x
    private val cy: Float  // Principal point y
) {
    data class Point3D(val x: Float, val y: Float, val z: Float)

    fun generatePointCloud(depthMap: Bitmap): List<Point3D> {
        val width = depthMap.width
        val height = depthMap.height
        val points = mutableListOf<Point3D>()

        for (v in 0 until height) {
            for (u in 0 until width) {
                val depth = depthMap.getPixel(u, v) and 0xFF // Extract depth value
                if (depth > 0) { // Skip invalid or zero-depth points
                    val z = depth / 255.0f // Normalize depth to a usable range
                    val x = (u - cx) * z / fx
                    val y = (v - cy) * z / fy
                    points.add(Point3D(x, y, z))
                }
            }
        }
        return points
    }
}